const express = require('express');
const router = express.Router();
const  { loginUser, logoutUser } = require('../controllers/tokenController');
const { isLoggedIn } = require('../middleware/auth'); 

/**
 * Logs in a user by validating credentials and issuing a token.
 * No authentication required.
 */
router.post('/login', loginUser);

/**
 * Logs out the currently authenticated user.
 * Only allowed if the user is logged in and acting on their own account.
 */
router.post('/logout', isLoggedIn, logoutUser);

module.exports = router;
