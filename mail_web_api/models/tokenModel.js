const { tokenStorage } = require('../storage/tokenStorage');
const { users } = require('../storage/userStorage');

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

/**
 * Validate credentials using email and password.
 */
function validateCredentials(email, password) {
    for (const user of users.values()) {
        if (user.email === email && user.password === password) {
            return user;
        }
    }
    return null;
}


module.exports = {
    generateToken,
    storeToken,
    getUserIdFromToken,
    deleteToken,
    isValidToken,
    validateCredentials
};
