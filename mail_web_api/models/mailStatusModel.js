const { mails } = require('../storage/mailStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');
const { formatMailSummary } = require('./mailModel');

function initializeSenderStatus(mailId, userId, isDraft) {
    if (!userMailStatus.has(userId)) userMailStatus.set(userId, new Map());

    userMailStatus.get(userId).set(mailId, {
        type: 'sent',
        isStar: false,
        isDraft: isDraft,
        labels: []
    });
}

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

function getMailStatus(mailId, userId) {
    return userMailStatus.get(userId)?.get(mailId) || null;
}

function addLabel(mailId, userId, labelId) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;             
    if (status.isSpam) return -1;         

    if (!status.labels.includes(labelId)) {
        status.labels.push(labelId);
    }

    return 0;
}

function removeLabel(mailId, userId, labelId) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;             
    if (status.isSpam) return -1;

    status.labels = status.labels.filter(id => id !== labelId);
    return true;
}

function markAsRead(mailId, userId) {
    const status = getMailStatus(mailId, userId);
    if (status?.type === 'received') status.isRead = true;
}

function toggleStar(mailId, userId) {
    const status = getMailStatus(mailId, userId);
    if (status && !status.isSpam) {
        status.isStar = !status.isStar;
    }
}

function setSpamStatus(mailId, userId, isSpam) {
    const status = getMailStatus(mailId, userId);
    if (!status) return null;

    status.isSpam = isSpam;

    if (isSpam) {
        status.labels = [];
        status.isStar = false;
    }

    return true;
}

function getFilteredMails(userId, filterFn, offset, limit) {
    const map = userMailStatus.get(userId);
    if (!map) return [];

    const result = [];

    for (const [mailId, status] of map.entries()) {
        const mail = mails.get(mailId);
        if (mail && filterFn(status, mail)) {
            result.push(mail);
        }
    }

    return result
        .sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt))
        .slice(offset, offset + limit)
        .map(mail => formatMailSummary(mail, userId));
}

function getInboxMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'received' && !s.isSpam, offset, limit);
}

function getSentMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'sent' && !s.isDraft, offset, limit);
}

function getStarredMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.isStar && !s.isSpam, offset, limit);
}

function getAllNonSpamMails(userId, offset = 0, limit = 50) {
    return getFilteredMails(userId, s => !s.isSpam, offset, limit);
}

function getSpamMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.isSpam, offset, limit);
}

function getDraftMails(userId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s => s.type === 'sent' && s.isDraft, offset, limit);
}

function getMailsByLabel(userId, labelId, limit = 50, offset = 0) {
    return getFilteredMails(userId, s =>
        !s.isSpam && Array.isArray(s.labels) && s.labels.includes(labelId),
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
    setSpamStatus
};


