const { users } = require('../storage/userStorage');
const { userLabels } = require('../storage/labelStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');
const { isValidSystemEmail, getUserIdFromEmail } = require('../utils/emailUtils');

/**
 * Remove the password field from a user object.
 */
function stripPassword(user) {
    if (!user) return null;
    const { password: _, ...userWithoutPassword } = user;
    return userWithoutPassword;
}

/**
 * Create and add a new user with a string userId.
 * Also initializes empty labels and mails structures for the user.
 */
function createUser({
    userId,
    name,
    password,
    gender,
    birthDate,
    profileImage
}) {
    if (users.has(userId)) {
        throw new Error("That username is taken. Try another.");
    }

    const newUser = {
        userId,
        name,
        password, // stored internally
        gender,
        birthDate,
        profileImage
    };

    users.set(userId, newUser);

    // Initialize userLabels and userMailIds
    userLabels.set(userId, []);
    userMailStatus.set(userId, new Map());

    return stripPassword(newUser);
}

/**
 * Get user by ID (without password).
 */
function getUserById(userId) {
    return stripPassword(users.get(userId.toLowerCase()));
}

/**
 * Finds a user based on their system email (userId@mailme.com).
 * Only emails that belong to the system domain are accepted.
 */
function getUserByEmail(email) {
    const normalizedEmail = email.trim().toLowerCase();

    // 1. Check if email matches system domain
    if (!isValidSystemEmail(normalizedEmail)) {
        return null;
    }

    // 2. Extract userId and look up in users map
    const userId = getUserIdFromEmail(normalizedEmail);
    if (!userId) return null;

    const user = users.get(userId);
    return user ? stripPassword(user) : null;
}

module.exports = {
    createUser,
    getUserById,
    getUserByEmail, 
    stripPassword
};
