const { users } = require('../storage/userStorage');
const { generateToken } = require('../models/tokenModel');

/**
 * Authenticate user and return JWT token.
 */
function loginUser(req, res) {
    const { userId, password } = req.body;

    const user = users.get(userId);
    if (!user || user.password !== password) {
        return res.status(401).json({ error: 'Invalid credentials' });
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

    return res.status(200).json({ message: 'Logged out successfully' });
}


module.exports = {
    loginUser,
    logoutUser
};