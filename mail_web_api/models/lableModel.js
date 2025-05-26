const { userLabels } = require('../storage/labelStorage');

let idCounter = 0;

/**
 * Creates a new label for the user.
 * Returns:
 *   - { id, name } if successful
 *   - null if label name already exists for this user
 */
function createLabel(name, userId) {
  const labels = userLabels.get(userId);
  if (labels.some(label => label.name === name)) return null;

  const label = { id: ++idCounter, name };
  labels.push(label);
  return label;
}

/**
 * Gets all labels for a user.
 * Returns Array of { id, name } if exists

 */
function getLabelsByUser(userId) {
  return userLabels.get(userId);
}

/**
 * Gets a label by id for a user.
 * Returns:
 *   - { id, name } if found
 *   - null if not found
 */
function getLabelById(userId, labelId) {
  const labels = userLabels.get(userId);
  return labels.find(label => label.id === labelId) || null;
}

/**
 * Renames a label.
 * Returns:
 *   - 0 if successful
 *   - -1 if label not found 
 *   - null if name already taken by another label
 */
function renameLabel(userId, labelId, newName) {
  const labels = userLabels.get(userId);

  if (labels.some(label => label.name === newName)) return null;

  const label = labels.find(l => l.id === labelId);
  if (!label) return -1;

  label.name = newName;
  return 0;
}

/**
 * Deletes a label.
 * Returns:
 *   - 0 if successful
 *   - -1 if label 
 */
function deleteLabel(userId, labelId) {
  const labels = userLabels.get(userId);

  const index = labels.findIndex(l => l.id === labelId);
  if (index === -1) return -1;

  labels.splice(index, 1);
  return 0;
}

module.exports = {
  createLabel,
  getLabelsByUser,
  getLabelById,
  renameLabel,
  deleteLabel
};
