const { createUser, getUserById, getPublicUserById } = require('../services/userService');
const { resolveProfileImagePath } = require('../services/profileImageService');
const { getUserIdFromEmail } = require('../utils/emailUtils');
const { isValidDate } = require('../utils/dateUtils');

const ALLOWED_GENDERS = ['MALE', 'FEMALE', 'OTHER'];

/**
 * POST /api/users
 * Register a new user with validations.
 */
async function registerUser(req, res) {
  try {
    let { userId, name, password, gender, birthDate } = req.body;

    if (!userId || userId.trim() === '') {
      return res.status(400).json({ error: 'Missing user ID' });
    }
    userId = userId.toLowerCase();

    if (!name || name.trim() === '') {
      return res.status(400).json({ error: 'Enter first name' });
    }
    if (!password || password.length < 8) {
      return res.status(400).json({ error: 'Use 8 characters or more for your password' });
    }
    if (!gender || gender.trim() === '') {
      return res.status(400).json({ error: 'Please select your gender' });
    }
    if (!ALLOWED_GENDERS.includes(gender.toUpperCase())) {
      return res.status(400).json({ error: 'Gender must be MALE, FEMALE or OTHER' });
    }
    if (!birthDate) {
      return res.status(400).json({ error: 'Please fill in a complete birthday' });
    }

    const parsedDate = new Date(birthDate);
    const day = parsedDate.getDate();
    const month = parsedDate.getMonth() + 1;
    const year = parsedDate.getFullYear();

    if (isNaN(parsedDate.getTime()) || !isValidDate(day, month, year)) {
      return res.status(400).json({ error: 'Please enter a valid, non-future date' });
    }

    const existingUser = await getUserById(userId);
    if (existingUser) {
      return res.status(400).json({ error: 'That username is taken. Try another.' });
    }

    const profileImage = resolveProfileImagePath(userId);

    const user = await createUser({
      userId,
      name,
      password,
      gender: gender.toUpperCase(),
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
  try {
    const userId = req.params.id;
    const user = await getUserById(userId);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    res.json(user);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/users/:id/public
 * Retrieve public info (used for availability check or UI display)
 */
async function getPublicUserInfo(req, res) {
  try {
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
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

module.exports = {
  registerUser,
  getUserDetails,
  getPublicUserInfo
};
