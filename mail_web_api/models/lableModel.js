const { labels } = require('../storage/labelStorage');
const { userLabelOwnership } = require('../storage/userLabelOwnershipStorage');
const { userMailLabelAssignments } = require('../storage/userMailLabelAssignmentStorage');

let idCounter = 0;

/**
 * Creates a new label and assigns it to the user.
 * Returns:
 *   - { id, name } if successful
 *   - null if a label with the same name already exists
 *   - -1 if the userId does not exist in the system
 */
function createLabel(name, userId) {
    if (!userLabelOwnership.has(userId)) return -1; 

    for (const existingName of labels.values()) {
      if (existingName === name) return null; 
    }

    let labelId = ++idCounter;
    labels.set(labelId, name);
    userLabelOwnership.get(userId).add(labelId);
    return { id: labelId, name };
}

/**
 * Returns all labels associated with a given user.
 * Returns:
 *   - Array of { id, name } objects
 *   - null if the userId is not found
 */
function getLabelsByUser(userId) {
  const labelIds = userLabelOwnership.get(userId);
  if (!labelIds) return null;

  return [...labelIds]
    .map(labelId => {
      const name = labels.get(labelId);
      return name ? { id: labelId, name } : null;
    })
    .filter(Boolean);
}

/**
 * Returns a label object by its ID.
 * Returns:
 *   - { id, name } if the label exists
 *   - null if not found
 */
function getLabelById(labelId) {
  const name = labels.get(labelId);
  return name ? { id: labelId, name } : null;
}

/**
 * Renames an existing label.
 * Returns:
 *   - 0 if successful
 *   - -1 if the labelId does not exist
 */
function renameLabel(labelId, newName) {
    if (!labels.has(labelId)) {
        return -1;
    }
    labels.set(labelId, newName);
    return 0;
}


/**
 * Deletes a label from the system.
 * Removes the label from:
 *   - the main labels map
 *   - all users' ownership sets
 *   - user-to-mail label assignments
 * Returns:
 *   - 0 if successful
 *   - -1 if the labelId does not exist
 */
function deleteLabel(labelId) {
    if (!labels.has(labelId)) return -1;

    labels.delete(labelId);

    // Remove from all users
    for (const labelSet of userLabelOwnership.values()) {
      labelSet.delete(labelId);
    }

    // Remove from mail assignments
    for (const labelMap of userMailLabelAssignments.values()) {
      labelMap.delete(labelId);
    }

    return 0;
}

module.exports = {
  createLabel,
  getLabelsByUser,
  getLabelById,
  renameLabel,
  deleteLabel
};