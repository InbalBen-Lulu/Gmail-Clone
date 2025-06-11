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
function processRecipients(to, isDraft, res) {
    const validRecipients = [];
    const invalidRecipients = [];

    for (const email of to) {
        if (!isValidSystemEmail(email)) {
            invalidRecipients.push(email);
            continue;
        }

        const userId = getUserIdFromEmail(email);
        const user = getUserById(userId);

        if (user) {
            validRecipients.push(userId);
        } else {
            invalidRecipients.push(email);
        }
    }

    if (!isDraft && validRecipients.length === 0) {
        res.status(400).json({
            error: 'Mail was not sent. None of the recipients exist.',
            invalidEmails: invalidRecipients
        });
        return null;
    }

    const responseMeta = {};
    if (invalidRecipients.length > 0) {
        responseMeta.warning = 'Mail was not sent to some addresses because they do not exist';
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
  return await checkUrlsAgainstBlacklist(words);
}

module.exports = {
    processRecipients,
    isContentBlacklisted
};
