const express = require('express');
const router = express.Router();
const { isLoggedIn } = require('../middleware/auth'); 

console.log("isLoggedIn type:", typeof isLoggedIn);

const {
  registerUser,
  getUserDetails
} = require('../controllers/userController');

// Create a new user (registration)
router.post('/', registerUser);

// Get user details by ID
router.get('/:id', isLoggedIn, getUserDetails);

module.exports = router;
