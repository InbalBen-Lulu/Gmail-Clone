const { mails } = require('../storage/mailStorage');
const { userMailIds } = require('../storage/userMailsStorage');
const { getUserById } = require('./userModel');
const { checkUrlsAgainstBlacklist } = require('./blackListModel');

let mailIdCounter = 0;

/**
 * Creates a new mail and stores it for all recipients and the sender.
 * Returns:
 *   - the created mail object (raw form)
 *   - null if blacklisted content
 */
function createMail(fromUserId, toUserIds, subject = '', body = '') {
    const words = (subject + ' ' + body).split(/\s+/);
    if (checkUrlsAgainstBlacklist(words)) return null;

    const mailId = ++mailIdCounter;

    const mail = {
        id: mailId,
        from: fromUserId,
        to: toUserIds,
        subject,
        body,
        sentAt: Date.now()
    };

    mails.set(mailId, mail);

    if (!userMailIds.has(fromUserId)) userMailIds.set(fromUserId, new Set());
    userMailIds.get(fromUserId).add(mailId);

    for (const userId of toUserIds) {
        if (!userMailIds.has(userId)) userMailIds.set(userId, new Set());
        userMailIds.get(userId).add(mailId);
    }

    return mail;
}

/**
 * Retrieves a mail in full view format, only if the user is a sender or recipient.
 * Returns null otherwise.
 */
function getMailById(mailId, userId) {
    const mail = mails.get(mailId);
    if (!mail) return null;

    const isSender = mail.from === userId;
    const isRecipient = mail.to.includes(userId);

    return (isSender || isRecipient) ? formatFullMail(mailId) : null;
}

/**
 * Returns up to `limit` mails associated with the user, newest first.
 */
function getMailsForUser(userId, limit = 50) {
    const mailIds = userMailIds.get(userId);
    if (!mailIds) return [];

    return [...mailIds]
        .filter(id => mails.has(id))
        .sort((a, b) => mails.get(b).sentAt - mails.get(a).sentAt)
        .slice(0, limit)
        .map(id => formatMailSummary(id))
        .filter(Boolean);
}

/**
 * Deletes a mail for a specific user (from their inbox only).
 */
function deleteMail(mailId, userId) {
    const mailSet = userMailIds.get(userId);
    if (!mailSet || !mailSet.has(mailId)) return false;

    mailSet.delete(mailId);
    return true;
}

/**
 * Updates subject and/or body of a mail after blacklist check.
 */
function updateMail(mailId, userId, updatedFields) {
    const mail = mails.get(mailId);
    if (!mail) return false;

    // Only sender is allowed to update
    if (mail.from !== userId) return false;

    const subject = updatedFields.subject ?? mail.subject;
    const body = updatedFields.body ?? mail.body;

    const words = (subject + ' ' + body).split(/\s+/);
    if (checkUrlsAgainstBlacklist(words)) return false;

    mail.subject = subject;
    mail.body = body;

    return true;
}

/**
 * Searches mails by query for a user. Returns formatted summaries.
 */
function searchMails(userId, query) {
    const q = String(query).trim().toLowerCase();

    // query is empty string or just spaces
    if (!q) return [];

    const mailIds = userMailIds.get(userId);
    if (!mailIds) return [];

    return [...mailIds]
        .map(id => mails.get(id))
        .filter(Boolean)
        .filter(mail =>
            Object.values(mail).some(val =>
                typeof val === 'string' && val.toLowerCase().includes(q)
            )
        )
        .map(mail => formatMailSummary(mail.id))
        .filter(Boolean);
}

/**
 * Returns the full view of a mail (for use by controller), including sender and recipients.
 * No userId is included, only name + email.
 */
function formatFullMail(mailId) {
    const mail = mails.get(mailId);
    if (!mail) return null;

    const fromUser = getUserById(mail.from);
    const toUsers = mail.to.map(getUserById).filter(Boolean);

    return {
        id: mail.id,
        subject: mail.subject,
        body: mail.body,
        sentAt: mail.sentAt,
        from: {
            name: `${fromUser.firstName} ${fromUser.lastName}`,
            email: fromUser.email
        },
        to: toUsers.map(u => ({
            name: `${u.firstName} ${u.lastName}`,
            email: u.email
        }))
    };
}

/**
 * Returns the summarized view of a mail (used for inbox/search).
 * No userId, only names.
 */
function formatMailSummary(mailId) {
    const mail = mails.get(mailId);
    if (!mail) return null;

    const fromUser = getUserById(mail.from);
    const toUsers = mail.to.map(getUserById).filter(Boolean);

    return {
        id: mail.id,
        subject: mail.subject,
        sentAt: mail.sentAt,
        from: `${fromUser.firstName} ${fromUser.lastName}`,
        to: toUsers.map(u => `${u.firstName} ${u.lastName}`)
    };
}

module.exports = {
    createMail,
    getMailById,
    getMailsForUser,
    deleteMail,
    updateMail,
    searchMails
};
