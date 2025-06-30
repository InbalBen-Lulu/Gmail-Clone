const Label = require('../models/labelModel');
// const { removeLabelFromMailStatuses } = require('./mailStatusService');

const DEFAULT_LABEL_COLOR = '#808080';

/**
 * Creates a new label for the user.
 * Returns:
 *   - label object if successful
 *   - null if label name already exists for this user
 */
async function createLabel(name, userId) {
  const existing = await Label.findOne({ userId, name });
  if (existing) return null;

  const label = new Label({ userId, name, color: DEFAULT_LABEL_COLOR });
  await label.save();
  return label.toJSON(); 
}

/**
 * Gets all labels for a user.
 */
async function getLabelsByUser(userId) {
  return await Label.find({ userId }).sort({ name: 1 });
}

/**
 * Gets a label by id for a user.
 */
async function getLabelById(userId, labelId) {
  return await Label.findOne({ userId, _id: labelId });
}

/**
 * Renames a label.
 * Returns:
 *   - 0 if successful
 *   - -1 if label not found 
 *   - null if name already taken by another label
 */
async function renameLabel(userId, labelId, newName) {
  const nameTaken = await Label.findOne({ userId, name: newName });
  if (nameTaken) return null;

  const updated = await Label.findOneAndUpdate(
    { userId, _id: labelId },
    { name: newName },
    { new: true }
  );

  return updated ? 0 : -1;
}

/**
 * Deletes a label and removes it from all mail statuses.
 * Returns:
 *   - 0 if successful
 *   - -1 if label not found
 */
async function deleteLabel(userId, labelId) {
  const deleted = await Label.findOneAndDelete({ userId, _id: labelId });
  if (!deleted) return -1;

  if (removeLabelFromMailStatuses) {
    await removeLabelFromMailStatuses(userId, labelId);
  }

  return 0;
}

/**
 * Sets a new color for a label.
 * Returns true if success, false otherwise
 */
async function setLabelColor(userId, labelId, hexColor) {
  const updated = await Label.findOneAndUpdate(
    { userId, _id: labelId },
    { color: hexColor },
    { new: true }
  );
  return !!updated;
}

/**
 * Resets label color to default.
 */
async function resetLabelColor(userId, labelId) {
  return await setLabelColor(userId, labelId, DEFAULT_LABEL_COLOR);
}

/**
 * Checks whether a label exists for a given user.
 */
async function labelExistsForUser(userId, labelId) {
  return await Label.exists({ userId, _id: labelId });
}

/**
 * Retrieves all labels belonging to a specific user, filtered by a list of label IDs.
 */
async function getLabelsByIdsForUser(userId, labelIds) {
  return await Label.find({
    userId,
    _id: { $in: labelIds }
});
}

module.exports = {
  createLabel,
  getLabelsByUser,
  getLabelById,
  renameLabel,
  deleteLabel,
  setLabelColor,
  resetLabelColor,
  labelExistsForUser,
  getLabelsByIdsForUser
};
