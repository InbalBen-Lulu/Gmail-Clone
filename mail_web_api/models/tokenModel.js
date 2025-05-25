const { tokenStorage } = require('../storage/tokenStorage');

/**
 * Generates a simple token string.
 */
function generateToken(userId) {
    return `token-${userId}-${Date.now()}`;
}

/**
 * Stores the token with its associated userId.
 */
function storeToken(token, userId) {
    tokenStorage.set(token, userId);
}

/**
 * Retrieves userId from token.
 */
function getUserIdFromToken(token) {
    return tokenStorage.get(token) || null;
}

/**
 * Deletes a token (logout).
 */
function deleteToken(token) {
    tokenStorage.delete(token);
}

/**
 * Checks whether a token exists.
 */
function isValidToken(token) {
    return tokenStorage.has(token);
}

module.exports = {
    generateToken,
    storeToken,
    getUserIdFromToken,
    deleteToken,
    isValidToken
};
