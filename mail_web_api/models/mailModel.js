const { mails } = require('../storage/mailStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');
const { getUserById } = require('./userModel');
const { checkUrlsAgainstBlacklist } = require('./blackListModel');
const {
    initializeSenderStatus,
    initializeRecipientStatus,
    getMailStatus
} = require('./mailStatusModel');

let mailIdCounter = 0;

async function createMail(fromUserId, toUserIds, subject = '', body = '') {
    const words = (subject + ' ' + body).split(/\s+/); 
    const isBlacklisted = !isDraft && await checkUrlsAgainstBlacklist(words);

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

function getMailById(mailId, userId) {
    const mail = mails.get(mailId);
    const status = getMailStatus(mailId, userId);
    if (!mail || !status) return null;
    return formatFullMail(mail, userId);
}

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
 * Updates the subject and/or body of a mail after a blacklist check.
 * Returns:
 *   - null if mail doesn't exist or user is not the sender
 *   - -1 if new content contains blacklisted URLs
 *   - 0 on success
 */
async function updateMail(mailId, userId, updatedFields) {
    const mail = mails.get(mailId);
    if (!mail || mail.from !== userId) return null;

    const subject = updatedFields.subject ?? mail.subject;
    const body = updatedFields.body ?? mail.body;
    const words = (subject + ' ' + body).split(/\s+/);

    if (await checkUrlsAgainstBlacklist(words)) return -1;

    mail.subject = subject;
    mail.body = body;
    return 0;
}

function searchMails(userId, query, limit = 5, offset = 0) {
    const statusMap = userMailStatus.get(userId);
    if (!statusMap) return [];

    const q = String(query || '').trim().toLowerCase();
    if (!q) return [];

    return [...statusMap.entries()]
        .map(([id]) => mails.get(id))
        .filter(Boolean)
        .filter(mail =>
            Object.values(mail).some(val =>
                typeof val === 'string' && val.toLowerCase().includes(q)
            )
        )
        .sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt))
        .slice(offset, offset + limit)
        .map(mail => formatMailSummary(mail, userId));
}

function formatMailSummary(mail, viewerId) {
    const fromUser = getUserById(mail.from);
    const toUsers = mail.to.map(getUserById).filter(Boolean);
    const status = getMailStatus(mail.id, viewerId);

    const labelList = userLabels.get(viewerId) || [];
    const fullLabels = labelList.filter(l => (status?.labels || []).includes(l.id));

    return {
        id: mail.id,
        subject: mail.subject,
        body: mail.body,
        sentAt: mail.sentAt,
        from: fromUser.name,
        to: toUsers.map(u => u.name),
        labels: fullLabels,
        isStar: status?.isStar || false,
        isDraft: status?.isDraft || false
    };
}

function formatFullMail(mail, viewerId) {
    const fromUser = getUserById(mail.from);
    const toUsers = mail.to.map(getUserById).filter(Boolean);
    const status = getMailStatus(mail.id, viewerId);

    const labelList = userLabels.get(viewerId) || [];
    const fullLabels = labelList.filter(l => (status?.labels || []).includes(l.id));

    return {
        id: mail.id,
        subject: mail.subject,
        body: mail.body,
        sentAt: mail.sentAt,
        from: fromUser.name,
        to: toUsers.map(u => u.name),
        image: null,
        labels: fullLabels,
        isStar: status?.isStar || false
    };
}

module.exports = {
    createMail,
    getMailById,
    deleteMail,
    updateMail,
    searchMails,
    formatMailSummary,
    formatFullMail
};
