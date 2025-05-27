const { createUser, getUserById } = require('../models/userModel');

/**
 * POST /api/users
 * Create a new user with JSON body data.
 */
function registerUser(req, res) {
    const userData = req.body;
    const { firstName, lastName, email, password, dateOfBirth } = userData;

    if (!firstName || !lastName || !email || !password || !dateOfBirth) {
        return res.status(400).json({ error: 'Missing required user fields' });
    }

    let newUser;
    try {
        newUser = createUser(userData);
    } catch (err) {
        return res.status(400).json({ error: err.message });
    }

    const { password: _, ...userSafe } = newUser;

    res.status(201).json({
        message: 'User created successfully',
        user: userSafe
    });
}


/**
 * GET /api/users/:id
 * Retrieve user details by userId
 */
function getUserDetails(req, res) {
    const userId = Number(req.params.id);
    const user = getUserById(userId);

    if (!user) {
        return res.status(404).json({ error: 'User not found'});
    }

    // Exclude sensitive fields (like password) before returning the user object
    const { password, ...userSafe } = user;
    res.json(userSafe);
}

module.exports = {
    registerUser,
    getUserDetails
};
