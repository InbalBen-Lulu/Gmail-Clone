const express = require('express');
const router = express.Router();
const { loginUser } = require('../controllers/tokenController');

// Validates user credentials and returns userId if correct.
// Note: In this exercise, no token is returned yet — only userId.
router.post('/', loginUser);

module.exports = router;
