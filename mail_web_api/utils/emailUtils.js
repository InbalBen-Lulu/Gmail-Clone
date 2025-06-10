const { MAIL_DOMAIN } = require('../config/mailConfig');

/**
 * Returns the full email address for a given userId.
 * Example: "EXAMPLE" ➝ "EXAMPLE@mailme.com"
 * @param {string} userId 
 * @returns {string}
 */
function getEmailFromUserId(userId) {
    return `${userId}@${MAIL_DOMAIN}`;
}

/**
 * Extracts the userId from a full email address.
 * Example: "EXAMPLE@mailme.com" ➝ "EXAMPLE"
 * @param {string} email 
 * @returns {string|null}
 */
function getUserIdFromEmail(email) {
    const atIndex = email.indexOf('@');
    if (atIndex === -1) return null;

    const domain = email.slice(atIndex + 1);
    if (domain.toLowerCase() !== MAIL_DOMAIN.toLowerCase()) return null;

    return email.slice(0, atIndex);
}

module.exports = {
    getEmailFromUserId,
    getUserIdFromEmail
};
