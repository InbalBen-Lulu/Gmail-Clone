const express = require('express');
const router = express.Router();
const  { loginUser, logoutUser } = require('../controllers/tokenController');
const { isLoggedIn } = require('../middleware/auth'); 

// Validates user credentials and returns userId if correct.
// Note: In this exercise, no token is returned yet — only userId.
router.post('/login', loginUser);

router.post('/logout', isLoggedIn, logoutUser);

module.exports = router;
