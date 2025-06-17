const { createUser, getUserById, getPublicUserById } = require('../models/userModel');
const { resolveProfileImagePath } = require('../models/profileImageModel');
const { getUserIdFromEmail } = require('../utils/emailUtils');

/**
 * POST /api/users
 * Create a new user with JSON body data.
 */
function registerUser(req, res) {
    const userData = req.body;
    const { userId, name, password, gender, birthDate } = userData;

    userData.userId = userId.toLowerCase();

    if (!birthDate) {
    return res.status(400).json({ error: 'Please fill in a complete birthday' });
    }
    if (!gender) {
        return res.status(400).json({ error: 'Please select your gender' });
    }
    if (!name) {
        return res.status(400).json({ error: 'Enter first name' });
    }
    if (!password) {
        return res.status(400).json({ error: 'Enter a password' });
    }
    if (!userId) {
        return res.status(400).json({ error: 'Missing user ID' });
    }


    const profileImage = resolveProfileImagePath(userId);

    const userWithImage = {
        ...userData,
        profileImage
    };

    try {
        const newUser = createUser(userWithImage);
        return res.status(201).json(newUser);
    } catch (err) {
        return res.status(400).json({ error: err.message });
    }
}


/**
 * GET /api/users/:id
 * Retrieve user details by userId
 */
function getUserDetails(req, res) {
    const userId = req.params.id; 
    const user = getUserById(userId);

    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }

    res.json(user); 
}

/**
 * GET /api/users/:id/public
 * Retrieve user public details by userId
 */
function getPublicUserInfo(req, res) {
    let userId = req.params.id; 
    const emailBasedId = getUserIdFromEmail(userId);
    if (emailBasedId) {
        userId = emailBasedId;
    }

    const user = getPublicUserById(userId);

    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }

    res.json(user); 
}

module.exports = {
    registerUser,
    getUserDetails, 
    getPublicUserInfo
};
