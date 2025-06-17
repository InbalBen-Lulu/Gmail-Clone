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
    if (typeof email !== 'string') return email;

    const index = email.indexOf('@');
    if (index === -1) {
        return email; 
    }

    return email.slice(0, index); 
}

/**
 * Checks whether the email belongs to the system's configured mail domain.
 * Example: "user@mailme.com" ➝ true, "user@gmail.com" ➝ false
 * @param {string} email 
 * @returns {boolean}
 */
function isValidSystemEmail(email) {
    if (typeof email !== 'string') return false;
    const parts = email.trim().toLowerCase().split('@');
    if (parts.length !== 2) return false;

    return parts[1] === MAIL_DOMAIN.toLowerCase();
}

module.exports = {
    getEmailFromUserId,
    getUserIdFromEmail,
    isValidSystemEmail
};
