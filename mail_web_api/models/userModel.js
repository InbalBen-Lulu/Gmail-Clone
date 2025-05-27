const { users } = require('../storage/userStorage');

let userIdCounter = 0;

/**
 * Create and add a new user with a numeric ID.
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
