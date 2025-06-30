const { getUserById } = require('../services/userService');
const {
    saveProfileImage,
    removeProfileImage,
} = require('../services/profileImageService');

/**
 * Handles the upload of a user's profile image.
 * Expects a base64-encoded image string in the request body.
 */
async function uploadProfileImage(req, res) {
    const userId = req.params.id;
    const { image } = req.body;

    try {
        const user = await getUserById(userId);
        if (!user) return res.status(404).json({ error: 'User not found' });

        const result = await saveProfileImage(user, image);
        if (result.error) return res.status(400).json({ error: result.error });

        return res.status(200).json({
            message: 'Profile photo updated.',
            imageUrl: result.imageUrl || user.profileImage
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: 'Server error' });
    }
}

/**
 * Handles deletion of a user's uploaded profile image.
 * Replaces it with the default image based on the user's ID.
 */
async function deleteProfileImage(req, res) {
    const userId = req.params.id;

    try {
        const user = await getUserById(userId);
        if (!user) return res.status(404).json({ error: 'User not found' });

        const result = await removeProfileImage(user);

        return res.status(200).json({
            message: 'Profile photo updated.',
            imageUrl: result.imageUrl || user.profileImage
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: 'Server error' });
    }
}

module.exports = {
    uploadProfileImage,
    deleteProfileImage
};
