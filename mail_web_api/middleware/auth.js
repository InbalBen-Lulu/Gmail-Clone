const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;

/**
 * Express middleware to check if request has a valid JWT token.
 */
function isLoggedIn(req, res, next) {
    const authHeader = req.headers['authorization'];
    const headerToken = authHeader && authHeader.split(' ')[1]; // Bearer <token>
    const cookieToken = req.cookies?.token;
    const token = headerToken || cookieToken;
    
    if (!token) {
        return res.status(403).json({ error: 'Token required' });
    }

    try {
        const decoded = jwt.verify(token, SECRET_KEY);
        req.user = decoded;
        next();
    } catch (err) {
        return res.status(401).json({ error: 'Invalid token' });
    }
}

/**
 * Middleware that ensures the user is accessing their own account.
 * Safely compares user ID from token and route parameter (case-insensitive).
 */
function isSelf(req, res, next) {
    const tokenUserId = req.user?.userId;
    const requestedId = req.params?.id;

    if (!tokenUserId || !requestedId) {
        return res.status(400).json({ error: 'Missing user ID for validation' });
    }

    if (tokenUserId.toLowerCase() !== requestedId.toLowerCase()) {
        return res.status(403).json({ error: 'Access denied: You can only access your own account' });
    }

    next();
}

module.exports = {
    isLoggedIn,
    isSelf
};
