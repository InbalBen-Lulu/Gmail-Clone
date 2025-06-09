const { users } = require('../storage/userStorage');
const { userLabels } = require('../storage/labelStorage');
const { userMailIds } = require('../storage/userMailsStorage');

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
        throw new Error("UserID already exists");
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
    userMailIds.set(userId, new Set());

    return stripPassword(newUser);
}

/**
 * Get user by ID (without password).
 */
function getUserById(userId) {
    return stripPassword(users.get(userId));
}

/**
 * Get a user by email (without password), or return null if not found.
 */
function getUserByEmail(email) {
    for (const user of users.values()) {
        if (user.email === email) {
            return stripPassword(user);
        }
    }
    return null;
}

module.exports = {
    createUser,
    getUserById,
    getUserByEmail, 
    stripPassword
};
