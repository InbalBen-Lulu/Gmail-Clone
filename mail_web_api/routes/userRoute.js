const express = require('express');
const router = express.Router();

const {
  registerUser,
  getUserDetails
} = require('../controllers/userController');

// Create a new user (registration)
router.post('/', registerUser);

// Get user details by ID
router.get('/:id', getUserDetails);

module.exports = router;
