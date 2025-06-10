const express = require('express');
const router = express.Router();
const { isLoggedIn } = require('../middleware/auth'); 
const {
    uploadProfileImage,
    deleteProfileImage
} = require('../controllers/profileImageController');
const {
  registerUser,
  getUserDetails
} = require('../controllers/userController');

// Create a new user (registration)
router.post('/', registerUser);

// Get user details by ID
router.get('/:id', isLoggedIn, getUserDetails);

// Upload a new profile image for the specified user
router.post('/:id/profile-image', isLoggedIn, uploadProfileImage);

// Delete the uploaded profile image and reset to default avatar
router.delete('/:id/profile-image', isLoggedIn, deleteProfileImage);

module.exports = router;
