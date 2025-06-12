const { userLabels } = require('../storage/labelStorage');
const { userMailStatus } = require('../storage/mailStatusStorage');

let idCounter = 0;
const DEFAULT_LABEL_COLOR = '#808080';

/**
 * Creates a new label for the user.
 * Returns:
 *   - { id, name } if successful
 *   - null if label name already exists for this user
 */
function createLabel(name, userId) {
  let labels = userLabels.get(userId);
  if (!labels) {
    labels = [];
    userLabels.set(userId, labels);
  }
  if (labels.some(label => label.name === name)) return null;

  const label = { id: ++idCounter, name, color: DEFAULT_LABEL_COLOR };  labels.push(label);
  return label;
}

/**
 * Gets all labels for a user.
 */
function getLabelsByUser(userId) {
  return userLabels.get(userId) || [];
}

/**
 * Gets a label by id for a user.
 * Returns:
 *   - { id, name } if found
 *   - null if not found
 */
function getLabelById(userId, labelId) {
  const labels = userLabels.get(userId) || [];
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
 * Deletes a label and removes it from all mail statuses.
 * Returns:
 *   - 0 if successful
 *   - -1 if label 
 */
function deleteLabel(userId, labelId) {
  const labels = userLabels.get(userId);
  const index = labels.findIndex(l => l.id === labelId);
  if (!labels || index === -1) return -1;

  labels.splice(index, 1);

  // Remove the label from all mail statuses of the user
  const mailMap = userMailStatus.get(userId);
  if (mailMap) {
    for (const status of mailMap.values()) {
      if (Array.isArray(status.labels)) {
        status.labels = status.labels.filter(id => id !== labelId);
      }
    }
  }

  return 0;
}

/**
 * Sets a new color for a label.
 * Returns:
 *   - true if success
 *   - false if label not found
 */
function setLabelColor(userId, labelId, hexColor) {
  const label = getLabelById(userId, labelId);
  if (!label) return false;

  label.color = hexColor;
  return true;
}

/**
 * Resets label color to default.
 * Returns:
 *   - true if success
 *   - false if label not found
 */
function resetLabelColor(userId, labelId) {
  const label = getLabelById(userId, labelId);
  if (!label) return false;

  label.color = DEFAULT_LABEL_COLOR;
  return true;
}

/**
 * Checks whether a label exists for a given user.
 * Returns:
 *   - true if the label exists
 *   - false otherwise
 */
function labelExistsForUser(userId, labelId) {
    const labels = userLabels.get(userId) || [];
    return labels.some(label => label.id === Number(labelId));
}

module.exports = {
  labelExistsForUser,
  createLabel,
  getLabelsByUser,
  getLabelById,
  renameLabel,
  setLabelColor,
  resetLabelColor,
  deleteLabel
};
