const MailStatus = require('../models/mailStatusModel');
const { getPublicUserById } = require('./userService');
const { getLabelsByIdsForUser } = require('./labelMailUtils');
const Mail = require('../models/mailModel');

/**
 * Creates a mail status entry for the sender of a mail.
 * Marks the mail as 'sent' and optionally as a draft.
 */
async function initializeSenderStatus(mailId, userId, isDraft) {
  await MailStatus.create({
    mailId,
    userId,
    type: 'sent',
    isDraft,
    isSpam: false,
    isRead: false
  });
}

/**
 * Creates a mail status entry for a recipient of a mail.
 * Marks the mail as 'received' and optionally as spam.
 */
async function initializeRecipientStatus(mailId, userId, isSpam = false) {
  await MailStatus.create({
    mailId,
    userId,
    type: 'received',
    isSpam,
    isRead: false,
    isDraft: false
  });
}

/**
 * Retrieves the mail status for a given user and mail.
 * Returns a plain JS object or null if not found.
 */
async function getMailStatus(mailId, userId) {
  return await MailStatus.findOne({ mailId, userId }).lean();
}

/**
 * Returns all mail status entries for a given user.
 * Used to fetch all visible mail IDs.
 */
async function getAllStatusesForUser(userId) {
  return await MailStatus.find({ userId }, 'mailId').lean();
}

/**
 * Deletes a specific mail status entry from the database.
 */
async function deleteMailStatus(mailId, userId) {
  return await MailStatus.deleteOne({ mailId, userId });
}

/**
 * Adds a label to the mail status if it is not marked as spam.
 * Returns:
 *   - 0 on success
 *   - -1 if status is spam
 *   - null if status not found
 */
async function addLabel(mailId, userId, labelId) {
  const status = await MailStatus.findOne({ mailId, userId });
  if (!status) return null;
  if (status.isSpam) return -1;

  // Avoid duplicates using equals()
  const alreadyExists = status.labels.some(id => id.equals(labelId));
  if (!alreadyExists) {
    status.labels.push(labelId);
    await status.save();
  }

  return 0;
}

/**
 * Removes a label from the mail status if it is not spam.
 * Returns:
 *   - true on success
 *   - -1 if status is spam
 *   - null if status not found
 */
async function removeLabel(mailId, userId, labelId) {
  const status = await MailStatus.findOne({ mailId, userId });
  if (!status) return null;
  if (status.isSpam) return -1;

  // Use $pull for atomic removal
  await MailStatus.updateOne(
    { mailId, userId },
    { $pull: { labels: labelId } }
  );

  return true;
}

/**
 * Marks a received mail as read.
 * Has no effect if the mail was sent by the user.
 */
async function markAsRead(mailId, userId) {
  await MailStatus.updateOne(
    { mailId, userId, type: 'received' },
    { $set: { isRead: true } }
  );
}

/**
 * Toggles the 'starred' status of a mail.
 * Has no effect if the mail is marked as spam.
 */
async function toggleStar(mailId, userId) {
  const status = await MailStatus.findOne({ mailId, userId });
  if (!status || status.isSpam) return;

  status.isStar = !status.isStar;
  await status.save();
}

/**
 * Sets or clears the spam status of a received mail.
 * When marked as spam, it also clears labels and removes star.
 * Returns:
 *   - true if updated
 *   - false if type is not 'received'
 *   - null if status not found
 */
async function setSpamStatus(mailId, userId, isSpam) {
  const status = await MailStatus.findOne({ mailId, userId });
  if (!status) return null;
  if (status.type !== 'received') return false;

  status.isSpam = isSpam;

  if (isSpam) {
    status.labels = [];
    status.isStar = false;
  }

  await status.save();
  return true;
}

/**
 * Formats a mail into a summary object for display.
 * Includes user-specific metadata like labels, star, read, etc.
 */
async function formatMailSummary(mail, userId) {
  const status = await getMailStatus(mail._id, userId);
  const fromUser = await getPublicUserById(mail.from);
  let labels = await getLabelsByIdsForUser(userId, status?.labels || []);

  // Convert labels using toJSON
  labels = labels.map(label =>
    typeof label.toJSON === 'function' ? label.toJSON() : label
  );

  const summary = {
    id: mail._id.toString(),
    subject: mail.subject,
    body: mail.body,
    sentAt: mail.sentAt,
    from: fromUser,
    to: mail.to,
    labels,
    isStar: status?.isStar || false,
    isDraft: status?.isDraft || false,
    isSpam: status?.isSpam || false,
    type: status?.type || 'sent',
  };

  if (status?.type === 'received') {
    summary.isRead = status.isRead || false;
  }

  return summary;
}

/**
 * Marks a sent draft as no longer a draft (isDraft = false).
 * Only applicable to mail statuses of the sender (type: 'sent').
 */
async function markDraftAsSent(mailId, userId) {
  await MailStatus.updateOne(
    { mailId, userId, type: 'sent' },
    { $set: { isDraft: false } }
  );
}


/**
 * Fetches, filters, sorts, and paginates mails based on a given status predicate.
 * Returns { total, mails[] } with formatted summaries.
 */
async function getFilteredMails(userId, filterFn, offset = 0, limit = 50) {
  const statuses = await MailStatus.find({ userId }).lean();
  const filteredStatuses = statuses.filter(filterFn);
  const mailIds = filteredStatuses.map(s => s.mailId);

  const mails = await Mail.find({ _id: { $in: mailIds } })
    .sort({ sentAt: -1 })
    .lean();

  const sorted = mails
    .filter(m => filteredStatuses.some(s => s.mailId.toString() === m._id.toString()))
    .slice(offset, offset + limit);

  const formatted = await Promise.all(
    sorted.map(mail => formatMailSummary(mail, userId))
  );

  return {
    total: filteredStatuses.length,
    mails: formatted,
  };
}

/**
 * Returns all non-spam received mails for the user.
 */
function getInboxMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s => s.type === 'received' && !s.isSpam, offset, limit);
}

/**
 * Returns all non-draft sent mails for the user.
 */
function getSentMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s => s.type === 'sent' && !s.isDraft, offset, limit);
}

/**
 * Returns all starred mails that are not spam.
 */
function getStarredMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s => s.isStar && !s.isSpam, offset, limit);
}

/**
 * Returns all non-spam mails, both sent and received.
 */
function getAllNonSpamMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s =>
    (s.type === 'received' && !s.isSpam) || s.type === 'sent',
    offset,
    limit
  );
}

/**
 * Returns all received mails that are marked as spam.
 */
function getSpamMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s => s.type === 'received' && s.isSpam === true, offset, limit);
}

/**
 * Returns all sent mails that are still drafts.
 */
function getDraftMails(userId, limit = 50, offset = 0) {
  return getFilteredMails(userId, s => s.type === 'sent' && s.isDraft, offset, limit);
}

/**
 * Returns all mails with a specific label that are not spam.
 */
function getMailsByLabel(userId, labelId, limit = 50, offset = 0) {
  const objectId = labelId.toString();
  return getFilteredMails(
    userId,
    s => !s.isSpam &&
        Array.isArray(s.labels) &&
        s.labels.some(id => id.equals?.(objectId)),
    offset,
    limit
  );
}

module.exports = {
  initializeSenderStatus,
  initializeRecipientStatus,
  getMailStatus,
  deleteMailStatus,
  addLabel,
  removeLabel,
  markAsRead,
  toggleStar,
  setSpamStatus,
  formatMailSummary,
  getInboxMails,
  getSentMails,
  getStarredMails,
  getAllNonSpamMails,
  getSpamMails,
  getDraftMails,
  getMailsByLabel,
  markDraftAsSent,
  getAllStatusesForUser
};
