const { mails } = require('../storage/mailStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');
const { userLabels } = require('../storage/labelStorage');
const { getPublicUserById } = require('./userModel');

/**
 * Initializes sender status for a new mail.
 * The mail is marked as 'sent' and optionally as a draft.
 */
function initializeSenderStatus(mailId, userId, isDraft) {
    if (!userMailStatus.has(userId)) userMailStatus.set(userId, new Map());

    userMailStatus.get(userId).set(mailId, {
        type: 'sent',
        isStar: false,
        isDraft: isDraft,
        labels: []
    });
}

/**
 * Initializes recipient status for a received mail.
 * The mail is marked as 'received' and optionally as spam.
 */
function initializeRecipientStatus(mailId, userId, isSpam = false) {
    if (!userMailStatus.has(userId)) userMailStatus.set(userId, new Map());

    userMailStatus.get(userId).set(mailId, {
        type: 'received',
        isStar: false,
        isSpam: isSpam,
        isRead: false,
        labels: []
    });
}

/**
 * Returns the status of a specific mail for a given user.
 * Returns:
 *   - the status object if found
 *   - null if not found
 */
function getMailStatus(mailId, userId) {
    return userMailStatus.get(userId)?.get(mailId) || null;
}

/**
 * Adds a label to the mail status.
 * Returns:
 *   - 0 if success
 *   - -1 if the mail is marked as spam
 *   - null if mail not found
 */
function addLabel(mailId, userId, labelId) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;
    if (status.isSpam) return -1;

    if (!status.labels.includes(labelId)) {
        status.labels.push(labelId);
    }

    return 0;
}

/**
 * Removes a label from the mail status.
 * Returns:
 *   - true if success
 *   - -1 if the mail is marked as spam
 *   - null if mail not found
 */
function removeLabel(mailId, userId, labelId) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;
    if (status.isSpam) return -1;

    status.labels = status.labels.filter(id => id !== labelId);
    return true;
}

/**
 * Marks a received mail as read.
 * Does nothing for sent mails or if mail not found.
 */
function markAsRead(mailId, userId) {
    const status = getMailStatus(mailId, userId);
    if (status?.type === 'received') status.isRead = true;
}

/**
 * Toggles the star status of a mail.
 * Does nothing if mail is spam or not found.
 */
function toggleStar(mailId, userId) {
    const status = getMailStatus(mailId, userId);
    if (status && !status.isSpam) {
        status.isStar = !status.isStar;
    }
}

/**
 * Sets the spam status of a mail.
 * If set to spam, removes all labels and unstars the mail.
 * Returns:
 *   - true if success
 *   - null if mail not found
 */
function setSpamStatus(mailId, userId, isSpam) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;

    if (status.type !== 'received') return false;

    status.isSpam = isSpam;

    if (isSpam) {
        status.labels = [];
        status.isStar = false;
    }

    return true;
}

/**
 * Formats a mail object for summary view (minimal), for a specific user.
 * Displays sender’s name and viewer-specific labels and status.
 * Returns:
 *   - object with id, subject, body, from (name), to (userIds), labels, isStar, isDraft
 */
function formatMailSummary(mail, viewerId) {
    const fromUser = getPublicUserById(mail.from);
    const status = getMailStatus(mail.id, viewerId);

    const labelList = userLabels.get(viewerId) || [];
    const fullLabels = labelList.filter(l => (status?.labels || []).includes(l.id));

    const summary = {
        id: mail.id,
        subject: mail.subject,
        body: mail.body,
        sentAt: mail.sentAt,
        from: fromUser,
        to: mail.to,
        labels: fullLabels,
        isStar: status?.isStar || false,
        isDraft: status?.isDraft || false,
        isSpam: status?.isSpam || false,
        type: status?.type || 'sent'
    };

    if (status?.type === 'received') {
        summary.isRead = status.isRead || false;
    }

    return summary;
}

/**
 * Filters and paginates mails for a user based on a given predicate.
 * Returns a list of summarized mails matching the filter.
 */
function getFilteredMails(userId, filterFn, offset, limit) {
    const map = userMailStatus.get(userId);
    if (!map) return { total: 0, mails: [] };

    const filtered = [];

    for (const [mailId, status] of map.entries()) {
        const mail = mails.get(mailId);
        if (mail && filterFn(status, mail)) {
            filtered.push(mail);
        }
    }

    const sorted = filtered.sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt));
    const sliced = sorted.slice(offset, offset + limit);
    const formatted = sliced.map(mail => formatMailSummary(mail, userId));

    return {
        total: filtered.length,
        mails: formatted
    };
}


/**
 * Returns up to 50 non-spam received mails for the user.
 */
function getInboxMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'received' && !s.isSpam, offset, limit);
}

/**
 * Returns up to 50 sent mails (not drafts) for the user.
 */
function getSentMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'sent' && !s.isDraft, offset, limit);
}

/**
 * Returns up to 50 starred mails that are not marked as spam.
 */
function getStarredMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.isStar && !s.isSpam, offset, limit);
}

/**
 * Returns up to 50 mails that are not marked as spam.
 */
function getAllNonSpamMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(
        userId,
        s => (s.type === 'received' && !s.isSpam) || s.type === 'sent',
        offset,
        limit
    );}

/**
 * Returns up to 50 mails that are marked as spam and were received by the user.
 */
function getSpamMails(userId, limit = 50, offset = 0) {
        return getFilteredMails(
        userId,
        s => s.type === 'received' && s.isSpam === true,
        offset,
        limit
    );
}

/**
 * Returns up to 50 sent mails that are still drafts.
 */
function getDraftMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'sent' && s.isDraft, offset, limit);
}

/**
 * Returns up to 50 mails associated with the given label and not marked as spam.
 */
function getMailsByLabel(userId, labelId, limit = 50, offset = 0) {
    const numericLabelId = Number(labelId);
    return getFilteredMails(userId, s =>
        !s.isSpam && Array.isArray(s.labels) && s.labels.includes(numericLabelId),
        offset,
        limit
    );
}

module.exports = {
    initializeSenderStatus,
    initializeRecipientStatus,
    getMailStatus,
    markAsRead,
    toggleStar,
    getInboxMails,
    getSentMails,
    getStarredMails,
    getAllNonSpamMails,
    getSpamMails,
    getDraftMails,
    addLabel,
    removeLabel,
    getMailsByLabel,
    setSpamStatus,
    formatMailSummary
};



