const { users } = require('../storage/userStorage');
const { generateToken } = require('../models/tokenModel');
const { getUserIdFromEmail } = require('../utils/emailUtils');

/**
 * Authenticate user and return JWT token.
 */
function loginUser(req, res) {
    let { userId, password } = req.body;

    // Try to extract userId from email format if necessary
    const emailBasedId = getUserIdFromEmail(userId);
    if (emailBasedId) {
        userId = emailBasedId;
    }

    const user = users.get(userId.toLowerCase());

    if (!user) {
        return res.status(401).json({ error: 'Enter a valid email.' });
    }

    if (user.password !== password) {
        return res.status(401).json({ error: 'Wrong password. Try again.' });
    }

    const token = generateToken(user);

    res.cookie('token', token, {
        httpOnly: true,
        secure: false,
        sameSite: 'Lax'
    });

    return res.status(200).json({ token, user });
}


/**
 * Clear the user's authentication cookie.
 */
function logoutUser(req, res) {
    res.clearCookie('token', {
        httpOnly: true,
        secure: false,
        sameSite: 'Strict',
        path: '/'
    });

    return res.status(200).json();
}


module.exports = {
    loginUser,
    logoutUser
};