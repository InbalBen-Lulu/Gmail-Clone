const { getFullUserForLogin, getUserById } = require('../services/userService');
const { generateToken } = require('../services/tokenService');
const { getUserIdFromEmail } = require('../utils/emailUtils');

/**
 * Authenticate user and return JWT token.
 */
async function loginUser(req, res) {
    let { userId, password } = req.body;

    // Try to extract userId from email if needed
    const emailBasedId = getUserIdFromEmail(userId);
    if (emailBasedId) {
        userId = emailBasedId;
    }

    let user = await getFullUserForLogin(userId.toLowerCase());

    if (!user) {
        return res.status(401).json({ error: 'Enter a valid email.' });
    }

    if (user.password !== password) {
        return res.status(401).json({ error: 'Wrong password. Try again.' });
    }

    const token = generateToken(user);

    res.cookie('token', token, {
        httpOnly: true,
        sameSite: 'lax',
        secure: false
    });

    user = await getUserById(userId.toLowerCase());
    return res.status(200).json({ token, user });
}

/**
 * Clear the user's authentication cookie.
 */
function logoutUser(req, res) {
    res.clearCookie('token');
    return res.status(200).json();
}

module.exports = {
    loginUser,
    logoutUser
};
