const { getUserById } = require('../models/userModel');

/**
 * Middleware that ensures a valid userId is provided in the request headers.
 * If valid, attaches the userId to req.userId and continues.
 * Otherwise, returns a 400 or 404 error response.
 */
function validateUserHeader(req, res, next) {
    const rawId = req.header('userId');
    const userId = Number(rawId);

    if (!rawId || isNaN(userId)) {
        return res.status(400).json({ error: 'Invalid or missing userId in request headers' });
    }

    const user = getUserById(userId);
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }

    req.userId = userId;
    next();
}

module.exports = validateUserHeader;
