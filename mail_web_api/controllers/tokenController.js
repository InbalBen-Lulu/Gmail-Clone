const { users } = require('../storage/userStorage');
const { generateToken } = require('../models/tokenModel');

/**
 * POST /api/tokens
 * Authenticate user and return JWT token.
 */
function loginUser(req, res) {
    const { userId, password } = req.body;

    const user = users.get(userId);
    if (!user || user.password !== password) {
        return res.status(401).json({ error: 'Invalid credentials' });
    }

    const token = generateToken(user);
    return res.status(200).json({token});
}

module.exports = {
    loginUser
}