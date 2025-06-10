const { getUserById } = require('../models/userModel');
const {
    saveProfileImage,
    removeProfileImage,
    resolveProfileImagePath
} = require('../models/profileImageModel');

/**
 * Handles the upload of a user's profile image.
 * Expects a base64-encoded image string in the request body.
 */
function uploadProfileImage(req, res) {
    const userId = req.params.id;
    const { image } = req.body;

    const user = getUserById(userId);
    if (!user) return res.status(404).json({ error: 'User not found' });

    const result = saveProfileImage(user, image);
    if (result.error) return res.status(400).json({ error: result.error });

    return res.status(200).json({ message: 'Profile image updated', path: user.profileImage });
}

/**
 * Handles deletion of a user's uploaded profile image.
 * Replaces it with the default image based on the user's ID.
 */
function deleteProfileImage(req, res) {
    const userId = req.params.id;
    const user = getUserById(userId);
    if (!user) return res.status(404).json({ error: 'User not found' });

    removeProfileImage(user);
    return res.status(200).json({ message: 'Profile image deleted' });
}

module.exports = {
    uploadProfileImage,
    deleteProfileImage
};
