const { createUser, getUserById, getPublicUserById } = require('../services/userService');
const { resolveProfileImagePath } = require('../services/profileImageService');
const { getUserIdFromEmail } = require('../utils/emailUtils');

/**
 * POST /api/users
 * Register a new user with validation
 */
async function registerUser(req, res) {
  const { userId, name, password, gender, birthDate } = req.body;

  if (!userId || userId.trim() === '') {
    return res.status(400).json({ error: 'Missing user ID' });
  }
  if (!name || name.trim() === '') {
    return res.status(400).json({ error: 'Enter first name' });
  }
  if (!password || password.length < 8) {
    return res.status(400).json({ error: 'Use 8 characters or more for your password' });
  }
  if (!gender || gender.trim() === '') {
    return res.status(400).json({ error: 'Please select your gender' });
  }
  if (!birthDate) {
    return res.status(400).json({ error: 'Please fill in a complete birthday' });
  }

  const parsedDate = new Date(birthDate);
  if (isNaN(parsedDate.getTime())) {
    return res.status(400).json({ error: 'Please enter a valid date' });
  }

  const normalizedUserId = userId.toLowerCase();
  const existingUser = await getUserById(normalizedUserId);
  if (existingUser) {
    return res.status(400).json({ error: 'That username is taken. Try another.' });
  }

  const profileImage = resolveProfileImagePath(normalizedUserId);

  try {
    const user = await createUser({
      userId: normalizedUserId,
      name,
      password,
      gender,
      birthDate: parsedDate,
      profileImage
    });

    return res.status(201).json(user);
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
}

/**
 * GET /api/users/:id
 * Retrieve full user by userId (no password)
 */
async function getUserDetails(req, res) {
  const userId = req.params.id;
  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }
  res.json(user);
}

/**
 * GET /api/users/:id/public
 * Retrieve public info (used for availability check or UI display)
 */
async function getPublicUserInfo(req, res) {
  let userId = req.params.id;
  const fromEmail = getUserIdFromEmail(userId);
  if (fromEmail) {
    userId = fromEmail;
  }

  const user = await getPublicUserById(userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  res.json(user);
}

module.exports = {
  registerUser,
  getUserDetails,
  getPublicUserInfo
};
