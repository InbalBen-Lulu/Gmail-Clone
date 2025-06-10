const fs = require('fs');
const path = require('path');
const { users } = require('../storage/userStorage');

/**
 * Saves a base64-encoded profile image for a given user.
 * Replaces any previously uploaded image.
 * 
 * @param {Object} user - The user object to update
 * @param {string} image - The base64-encoded image string
 * @returns {Object} result - Success or error object
 */
function saveProfileImage(user, image) {
    if (!image || !image.startsWith('data:image/')) {
        return { error: 'Invalid or missing image' };
    }

    const matches = image.match(/^data:image\/(png|jpeg|jpg);base64,(.+)$/);
    if (!matches) {
        return { error: 'Invalid image format' };
    }

    const ext = matches[1];
    const base64Data = matches[2];
    const buffer = Buffer.from(base64Data, 'base64');

    // delete old image if exists
    if (user.profileImage && user.profileImage.startsWith('/profilePics/uploads/')) {
        const oldPath = path.join(__dirname, '..', user.profileImage);
        if (fs.existsSync(oldPath)) fs.unlinkSync(oldPath);
    }

    const fileName = `${user.userId}.${ext}`;
    const filePath = path.join(__dirname, '..', 'profilePics', 'uploads', fileName);
    fs.mkdirSync(path.dirname(filePath), { recursive: true });
    fs.writeFileSync(filePath, buffer);

    user.profileImage = `/profilePics/uploads/${fileName}`;

    users.set(user.userId, user);

    return { success: true };
}

/**
 * Removes the uploaded profile image of the user (if exists)
 * and resets it to a default avatar based on userId.
 * 
 * @param {Object} user - The user object to update
 */
function removeProfileImage(user) {
    if (user.profileImage && user.profileImage.startsWith('/profilePics/uploads/')) {
        const imagePath = path.join(__dirname, '..', user.profileImage);
        if (fs.existsSync(imagePath)) fs.unlinkSync(imagePath);
    }
    user.profileImage = resolveProfileImagePath(user.userId);

    users.set(user.userId, user);
}

/**
 * Resolves the default profile image path based on the user's ID.
 * Uses the first character of the userId to pick an avatar, or fallback to default.
 * 
 * @param {string} userId - The user's ID
 * @returns {string} path - The resolved image path
 */
function resolveProfileImagePath(userId) {
    const firstChar = userId[0].toUpperCase();
    const assetPath = path.join(__dirname, '..', 'profilePics', 'assets', `${firstChar}.png`);
    return fs.existsSync(assetPath)
        ? `/profilePics/assets/${firstChar}.png`
        : `/profilePics/assets/default.png`;
}

module.exports = {
    saveProfileImage,
    removeProfileImage,
    resolveProfileImagePath
};
