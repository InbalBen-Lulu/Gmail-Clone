const { users } = require('../storage/userStorage');
const { generateToken } = require('../models/tokenModel');

/**
 * Authenticate user and return JWT token.
 */
function loginUser(req, res) {
    const { userId, password } = req.body;

    const user = users.get(userId.toLowerCase());

    if (!user || user.password !== password) {
        return res.status(401).json({ error: 'Wrong password. Try again.' });
    }

    const token = generateToken(user);

    res.cookie('token', token, {
        httpOnly: true,
        secure: false,
        sameSite: 'Strict'
    });

    return res.status(200).json();
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