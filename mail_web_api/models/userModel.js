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
    createUser,
    getUserById,
    validateCredentials
};
