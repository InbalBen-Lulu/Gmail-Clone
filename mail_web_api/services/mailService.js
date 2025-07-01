const Mail = require('../models/mailModel');
const { isContentBlacklisted } = require('../utils/mailUtils');
const { getPublicUserById } = require('./userService');
const { getLabelsByIdsForUser } = require('./labelMailUtils');
const {
  initializeSenderStatus,
  initializeRecipientStatus,
  getMailStatus,
  formatMailSummary,
  deleteMailStatus,
  markDraftAsSent,
  getAllStatusesForUser 
} = require('./mailStatusService.js');

/**
 * Creates a new mail and initializes sender + recipient statuses.
 */
async function createMail(fromUserId, toUserIds, subject = '', body = '', isDraft = false) {
  const isBlacklisted = await isContentBlacklisted(subject, body, isDraft);

  const mail = new Mail({
    from: fromUserId,
    to: toUserIds,
    subject,
    body,
    sentAt: new Date()
  });

  await mail.save();

  await initializeSenderStatus(mail.id, fromUserId, isDraft);

  if (!isDraft) {
    for (const id of toUserIds) {
      await initializeRecipientStatus(mail.id, id, isBlacklisted);
    }
  }

  return mail.toJSON();
}

/**
  * Retrieves a full mail by ID for a specific user.
 */
async function getMailById(mailId, userId) {
  const mail = await Mail.findById(mailId).lean();
  if (!mail) return null;

  const status = await getMailStatus(mailId, userId);
  if (!status) return null;

  return await formatFullMail(mail, userId, status);
}

/**
 * Deletes a mail for a specific user.
 * If it's a draft, deletes the mail from the database.
 */
async function deleteMail(mailId, userId) {
  const status = await getMailStatus(mailId, userId);
  if (!status) return false;

  if (status.isDraft) {
    await Mail.findByIdAndDelete(mailId);
  }

  await deleteMailStatus(mailId, userId);
  return true;
}

/**
 * Updates a draft mail’s fields for a specific user.
 * Only allowed if the user is the sender and the mail is still a draft.
 */
async function updateMail(mailId, userId, updatedFields) {
  const mail = await Mail.findById(mailId);
  if (!mail || mail.from !== userId) return null;

  const status = await getMailStatus(mailId, userId);
  if (!status || !status.isDraft) return null;

  mail.subject = updatedFields.subject;
  mail.body = updatedFields.body;
  mail.to = Array.isArray(updatedFields.to) ? updatedFields.to : [];

  await mail.save();
  return true;
}

/**
 * Sends a previously saved draft after updating its fields.
 * Updates sent time and initializes recipient statuses.
 */
async function sendDraft(mailId, userId, updatedFields) {
  const mail = await Mail.findById(mailId);
  if (!mail || mail.from !== userId) return null;

  const status = await getMailStatus(mailId, userId);
  if (!status?.isDraft) return null;

  mail.subject = updatedFields.subject;
  mail.body = updatedFields.body;
  mail.to = Array.isArray(updatedFields.to) ? updatedFields.to : [];
  mail.sentAt = new Date();

  await mail.save();

  status.isDraft = false;
  await markDraftAsSent(mailId, userId);

  const isSpam = await isContentBlacklisted(mail.subject, mail.body, false);
  for (const id of mail.to) {
    await initializeRecipientStatus(mail.id, id, isSpam);
  }

  return 0;
}

/**
 * Formats a mail object for full view by a specific user.
 */
async function formatFullMail(mail, userId, status) {
  const fromUser = await getPublicUserById(mail.from);
  let labels = await getLabelsByIdsForUser(userId, status?.labels || []);

  // Convert labels using toJSON
  labels = labels.map(label =>
    typeof label.toJSON === 'function' ? label.toJSON() : label
  );

  return {
    id: mail._id.toString(),
    subject: mail.subject,
    body: mail.body,
    sentAt: mail.sentAt,
    from: fromUser,
    to: mail.to,
    labels,
    isStar: status?.isStar || false,
    isSpam: status?.isSpam || false,
    type: status?.type || 'sent'
  };
}

/**
 * Searches for mails visible to the user, containing the query string.
 * Results are sorted by send date (descending).
 * Returns:
 *   - total mails count
 *   - list of formatted mail summaries
 * Uses a MongoDB $or query for efficient search.
 */
async function searchMails(userId, query, limit = 5, offset = 0) {
  const q = String(query || '').trim().toLowerCase();
  if (!q) return { total: 0, mails: [] };

  const statuses = await getAllStatusesForUser(userId);
  if (!statuses.length) return { total: 0, mails: [] };

  const mailIds = statuses.map(s => s.mailId);

  // Use MongoDB to filter matching mails directly
  const searchRegex = new RegExp(q, 'i');
  const mails = await Mail.find({
    _id: { $in: mailIds },
    $or: [
      { subject: searchRegex },
      { body: searchRegex },
      { to: { $elemMatch: { $regex: searchRegex } } },
      { from: { $regex: searchRegex } }
    ]
  }).sort({ sentAt: -1 }).skip(offset).limit(limit).lean();

  const formatted = await Promise.all(mails.map(mail => formatMailSummary(mail, userId)));

  const total = await Mail.countDocuments({
    _id: { $in: mailIds },
    $or: [
      { subject: searchRegex },
      { body: searchRegex },
      { to: { $elemMatch: { $regex: searchRegex } } },
      { from: { $regex: searchRegex } }
    ]
  });

  return {
    total,
    mails: formatted
  };
}

module.exports = {
  createMail,
  getMailById,
  deleteMail,
  updateMail,
  sendDraft,
  searchMails
};