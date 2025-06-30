const Label = require('../models/labelModel');
const MailStatus = require('../models/mailStatusModel');

/**
 * Retrieves all labels belonging to a specific user, filtered by a list of label IDs.
 */
async function getLabelsByIdsForUser(userId, labelIds) {
  return await Label.find({
    userId,
    _id: { $in: labelIds }
  }).lean();
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
