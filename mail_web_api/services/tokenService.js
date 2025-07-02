const jwt = require('jsonwebtoken');

const SECRET_KEY = process.env.SECRET_KEY;

/**
 * Generates a signed JWT token for the given user.
 */
function generateToken(user) {
    const payload = {
        userId: user.userId,
        name: user.name
    };

    const token = jwt.sign(payload, SECRET_KEY);

    return token;
}

module.exports = {
    generateToken
}