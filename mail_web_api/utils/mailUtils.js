const { isValidSystemEmail, getUserIdFromEmail } = require('./emailUtils');
const { getUserById } = require('../models/userModel');
const { checkUrlsAgainstBlacklist } = require('../models/blackListModel');

/**
 * Processes the 'to' field of a mail.
 * Filters valid recipients and prepares a warning for invalid ones.
 * 
 * Returns:
 *   - { validRecipients: number[], responseMeta: object }
 *   - null if no valid recipients and mail is not a draft
 */
function processRecipients(to, isDraft, res, fromUserId) {
    const validRecipients = [];
    const invalidRecipients = [];

    for (const email of to) {
        if (!isValidSystemEmail(email)) {
            invalidRecipients.push(email);
            continue;
        }

        const userId = getUserIdFromEmail(email);
        const user = getUserById(userId);

        if (!user) {
            invalidRecipients.push(email);
            continue;
        }

        if (userId === fromUserId) {
            invalidRecipients.push(email);
            continue;
        }

        validRecipients.push(userId);
    }

    if (!isDraft && validRecipients.length === 0) {
        res.status(400).json({
            error: 'Mail was not sent. None of the recipients exist (or you tried to send to yourself).',
            invalidEmails: invalidRecipients
        });
        return null;
    }

    const responseMeta = {};
    if (invalidRecipients.length > 0) {
        responseMeta.warning = 'Mail was not sent to some addresses because they do not exist or were invalid';
        responseMeta.invalidEmails = invalidRecipients;
    }

    return { validRecipients, responseMeta };
}


/**
 * Checks if any word in the subject or body is blacklisted.
 * Returns:
 *   - true if at least one word is blacklisted
 *   - false otherwise or if the mail is a draft
 */
async function isContentBlacklisted(subject, body, isDraft) {
  if (isDraft) return false;

    const words = (subject + ' ' + body).split(/\s+/);
    const decodedWords = words.map(word => {
        try {
            return decodeURIComponent(word);
        } catch (e) {
            return word;
        }
    });
  return await checkUrlsAgainstBlacklist(decodedWords);
}

module.exports = {
    processRecipients,
    isContentBlacklisted
};
