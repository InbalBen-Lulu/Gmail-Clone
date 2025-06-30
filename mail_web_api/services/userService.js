const User = require('../models/userModel');

/**
 * Create a new user in the database.
 */
async function createUser(userData) {
  const user = new User(userData);
  await user.save();
  const { password, ...userWithoutPassword } = user.toObject();
  return userWithoutPassword;
}

/**
 * Get full user (without password) by userId.
 */
async function getUserById(userId) {
  const user = await User.findOne({ userId: userId.toLowerCase() }).lean();
  if (!user) return null;
  const { password, ...userWithoutPassword } = user;
  return userWithoutPassword;
}

/**
 * Get only public info for a user by userId.
 */
async function getPublicUserById(userId) {
  const user = await User.findOne({ userId: userId.toLowerCase() }).lean();
  if (!user) return null;

  return {
    userId: user.userId,
    name: user.name,
    profileImage: user.profileImage
  };
}

/**
 * Get full user (with password) by userId.
 */
async function getFullUserForLogin(userId) {
  return await User.findOne({ userId: userId.toLowerCase() }).lean();
}

module.exports = {
  createUser,
  getUserById,
  getPublicUserById,
  getFullUserForLogin
};
