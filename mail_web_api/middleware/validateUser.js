const { getUserById } = require('../models/userModel');

/**
 * Middleware that ensures a valid userId is provided in the request headers.
 * If valid, attaches the userId to req.userId and continues.
 * Otherwise, returns a 400 or 404 error response.
 */
function validateUserHeader(req, res, next) {
  const userId = req.header('userId');

  // Check that the header exists
  if (!userId) {
    return res.status(400).json({ error: 'Missing userId in request headers' });
  }

  // Use model logic to check if user exists
  const user = getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  // Save the userId for use in routes/controllers
  req.userId = userId;
  next();
}

module.exports = validateUserHeader;
