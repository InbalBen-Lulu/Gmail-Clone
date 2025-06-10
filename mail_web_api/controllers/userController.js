const { createUser, getUserById } = require('../models/userModel');
const { resolveProfileImagePath } = require('../models/profileImageModel');

/**
 * POST /api/users
 * Create a new user with JSON body data.
 */
function registerUser(req, res) {
    const userData = req.body;
    const { userId, name, password, gender, birthDate } = userData;
    
    userData.userId = userId.toLowerCase();

    if (!userId || !name || !password || !gender || !birthDate) {
        return res.status(400).json({ error: 'Missing required user fields' });
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

module.exports = {
    registerUser,
    getUserDetails
};
