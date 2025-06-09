const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;

/**
 * Express middleware to check if request has a valid JWT token.
 */
function isLoggedIn(req, res, next) {
    const authHeader = req.headers.authorization;

    if (authHeader) {
        const token = authHeader.split(" ")[1];
        try {
            const decoded = jwt.verify(token, SECRET_KEY); 
            req.user = decoded; // Make decoded info available in request
            next();
        } catch (err) {
            return res.status(401).json({ error: 'Invalid token' });
        }
    } else {
        return res.status(403).json({ error: 'Token required' });
    }
}

module.exports = {
    isLoggedIn
};
