const express = require('express');
const router = express.Router();
const { isLoggedIn, isSelf } = require('../middleware/auth'); 
const {
    uploadProfileImage,
    deleteProfileImage
} = require('../controllers/profileImageController');
const {
  registerUser,
  getUserDetails,
  getPublicUserInfo
} = require('../controllers/userController');

// Create a new user (registration)
router.post('/', registerUser);

// Get user details by ID
router.get('/:id', isLoggedIn, isSelf, getUserDetails);

// Get user public details by ID
router.get('/:id/public', isLoggedIn, getPublicUserInfo);

// Upload a new profile image for the specified user
router.post('/:id/profile-image', isLoggedIn, isSelf, uploadProfileImage);

// Delete the uploaded profile image and reset to default avatar
router.delete('/:id/profile-image', isLoggedIn, isSelf, deleteProfileImage);

module.exports = router;
