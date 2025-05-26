    const { validateCredentials } = require('../models/tokenModel');

    /**
     * POST /api/tokens
     * Validate login and return userId if credentials are correct.
     */
    function loginUser(req, res) {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ error: 'Email and password are required' });
    }

    const user = validateCredentials(email, password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid email or password' });
    }

    res.status(200).json({ userId: user.userId });
    }

    module.exports = { loginUser };
