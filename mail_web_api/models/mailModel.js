const { mails } = require('../storage/mailStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');
const { isContentBlacklisted } = require('../utils/mailUtils');
const { userLabels } = require('../storage/labelStorage');
const { getUserById, getPublicUserById } = require('./userModel');
const {
    initializeSenderStatus,
    initializeRecipientStatus,
    getMailStatus,
    formatMailSummary
} = require('./mailStatusModel');

let mailIdCounter = 0;

/**
 * Creates a new mail and initializes status for sender and recipients.
 */
async function createMail(fromUserId, toUserIds, subject = '', body = '', isDraft = false) {
    const isBlacklisted = await isContentBlacklisted(subject, body, isDraft);

    const mailId = ++mailIdCounter;

    const mail = {
        id: mailId,
        from: fromUserId,
        to: toUserIds,
        subject,
        body,
        sentAt: new Date().toISOString()
    };

    mails.set(mailId, mail);
    initializeSenderStatus(mailId, fromUserId, isDraft);

    if (!isDraft) {
        const isSpam = isBlacklisted;
        toUserIds.forEach(id => initializeRecipientStatus(mailId, id, isSpam));
    }

    return mail;
}

/**
 * Retrieves a full mail by ID for a specific user, including labels and sender info.
 * Returns:
 *   - formatted full mail object if found
 *   - null if mail or status not found
 */
function getMailById(mailId, userId) {
    const mail = mails.get(mailId);
    const status = getMailStatus(mailId, userId);
    if (!mail || !status) return null;
    return formatFullMail(mail, userId);
}

/**
 * Deletes a mail for a specific user.
 * If it's a draft, it is deleted from the main storage as well.
 * Returns:
 *   - true if successfully deleted
 *   - false if status not found
 */
function deleteMail(mailId, userId) {
    const statusMap = userMailStatus.get(userId);
    const status = statusMap?.get(mailId);
    if (!status) return false;

    if (status.isDraft) {
        mails.delete(mailId);
    }
    statusMap.delete(mailId);
    return true;
}

/**
 * Updates a draft mail’s fields for a specific user.
 * Only allowed if the mail is still marked as draft.
 * Returns:
 *   - true if updated successfully
 *   - null if mail is not found or not editable
 */
function updateMail(mailId, userId, updatedFields) {
    const mail = mails.get(mailId);
    if (!mail || mail.from !== userId) return null;

    const status = getMailStatus(mailId, userId);
    if (!status || !status.isDraft) return null;

    mail.subject = updatedFields.subject;
    mail.body = updatedFields.body;
    mail.to = Array.isArray(updatedFields.to) ? updatedFields.to : [];

    return true;
}

/**
 * Formats a mail object for full view (detailed), for a specific user.
 * Displays sender’s name and profile image.
 * Returns:
 *   - object with id, subject, body, from (name), to (userIds), image, labels, isStar, isSpam
 */
function formatFullMail(mail, viewerId) {
    const fromUser = getPublicUserById(mail.from);
    const status = getMailStatus(mail.id, viewerId);

    const labelList = userLabels.get(viewerId) || [];
    const fullLabels = labelList.filter(l => (status?.labels || []).includes(l.id));

    return {
        id: mail.id,
        subject: mail.subject,
        body: mail.body,
        sentAt: mail.sentAt,
        from: fromUser,
        to: mail.to,
        labels: fullLabels,
        isStar: status?.isStar || false,
        isStar: status?.isStar || false,
        isSpam: status?.isSpam || false,
        type: status?.type || 'sent'
    };
}

/**
 * Sends a previously saved draft after updating its fields.
 * Updates sent time and initializes recipient status (spam or not).
 * Returns:
 *   - 0 on success
 *   - null if mail not found, not draft, or not owned by user
 */
async function sendDraft(mailId, userId, updatedFields) {
    const mail = mails.get(mailId);
    if (!mail || mail.from !== userId) return null;

    const status = getMailStatus(mailId, userId);
    if (!status?.isDraft) return null;

    // Update mail
    mail.subject = updatedFields.subject;
    mail.body = updatedFields.body;
    mail.to = Array.isArray(updatedFields.to) ? updatedFields.to : [];

    // Update sent time
    mail.sentAt = new Date().toISOString();

    // Update draft flag
    status.isDraft = false;

    // Blacklist check
    const isSpam = await isContentBlacklisted(mail.subject, mail.body, false);

    // Create status for recipients
    for (const userId of mail.to) {
        initializeRecipientStatus(mailId, userId, isSpam);
    }

    return 0;
}

/**
 * Searches for mails visible to the user, containing the query string.
 * Results are sorted by send date (descending).
 * Returns:
 *   - total mails count
 *   - list of formatted mail summaries
 */
function searchMails(userId, query, limit = 5, offset = 0) {
    const statusMap = userMailStatus.get(userId);
    if (!statusMap) return { total: 0, mails: [] };

    const q = String(query || '').trim().toLowerCase();
    if (!q) return { total: 0, mails: [] };

    const matched = [...statusMap.entries()]
        .map(([id]) => mails.get(id))
        .filter(Boolean)
        .filter(mail => {
            const subjectMatch = mail.subject?.toLowerCase().includes(q);
            const bodyMatch = mail.body?.toLowerCase().includes(q);
            const toMatch = Array.isArray(mail.to) &&
                mail.to.some(recipient => recipient.toLowerCase().includes(q));
            const fromMatch = mail.from?.toLowerCase().includes(q);

            const fromUser = getUserById(mail.from);
            const fromNameMatch = fromUser?.name?.toLowerCase().includes(q);

            return subjectMatch || bodyMatch || toMatch || fromMatch || fromNameMatch;
        })
        .sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt));

    const paginated = matched
        .slice(offset, offset + limit)
        .map(mail => formatMailSummary(mail, userId));

    return {
        total: matched.length,
        mails: paginated
    };
}


module.exports = {
    createMail,
    getMailById,
    deleteMail,
    updateMail,
    searchMails,
    sendDraft
};
