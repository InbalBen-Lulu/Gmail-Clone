const Label = require('../models/labelModel');
const MailStatus = require('../models/mailStatusModel');

/**
 * Retrieves all labels belonging to a specific user, filtered by a list of label IDs.
 * Returns plain objects with id, name, color, userId (no _id, no __v)
 */
async function getLabelsByIdsForUser(userId, labelIds) {
  const labels = await Label.find({
    userId,
    _id: { $in: labelIds }
  }).lean();

  return labels.map(label => ({
    id: label._id.toString(),
    name: label.name,
    color: label.color,
    userId: label.userId
  }));
}

/**
 * Removes a label from all mail statuses of a specific user.
 */
async function removeLabelFromMailStatuses(userId, labelId) {
  await MailStatus.updateMany(
    { userId },
    { $pull: { labels: labelId } }
  );
}

module.exports = {
  getLabelsByIdsForUser,
  removeLabelFromMailStatuses,
};
