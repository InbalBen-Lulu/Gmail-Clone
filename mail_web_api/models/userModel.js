const { users } = require('../storage/userStorage');
const { userLabels } = require('../storage/labelStorage');
const { userMailIds } = require('../storage/userMailsStorage');

let userIdCounter = 0;

/**
 * Create and add a new user with a numeric ID.
 * Also initializes empty labels and mails structures for the user.
 */
function createUser({
    firstName,
    lastName,
    email,
    password,
    dateOfBirth,
    gender = null,
    phoneNumber = null
}) {
    // Check for existing email
    for (const user of users.values()) {
        if (user.email === email) {
            throw new Error("Email already registered");
        }
    }

    const userId = ++userIdCounter;
    const newUser = {
        userId,
        firstName,
        lastName,
        email,
        password,
        dateOfBirth,
        gender,
        phoneNumber
    };

    users.set(userId, newUser);

    // Initialize userLabels and userMailIds with empty structures
    userLabels.set(userId, []);
    userMailIds.set(userId, new Set());

    return newUser;
}

/**
 * Get user by ID.
 */
function getUserById(userId) {
    return users.get(userId) || null;
}

/**
 * Get a user by email or return null if not found.
 */
function getUserByEmail(email) {
    for (const user of users.values()) {
        if (user.email === email) {
            return user;
        }
    }
    return null;
}

module.exports = {
    createUser,
    getUserById,
    getUserByEmail
};
