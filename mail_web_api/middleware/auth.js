const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;

/**
 * Express middleware to check if request has a valid JWT token.
 */
function isLoggedIn(req, res, next) {
    const token = req.cookies.token;

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

module.exports = {
    isLoggedIn
};
