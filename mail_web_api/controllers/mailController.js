const mailService = require('../services/mailService');
const mailStatusService = require('../services/mailStatusService');
const { labelExistsForUser } = require('../services/labelService');
const { addUrlsToBlacklist, removeUrlsFromBlacklist } = require('../services/blackListService');
const { processRecipients } = require('../utils/mailUtils');

/**
 * POST /api/mails
 * Creates a new mail and sends it to recipients.
 */
async function createMail(req, res) {
  try {
    const { to = [], subject = '', body = '', isDraft = false } = req.body;

    if (!isDraft && (!Array.isArray(to) || to.length === 0)) {
      return res.status(400).json({ error: 'Recipients are required for sending mails' });
    }

    const userId = req.user.userId.toLowerCase();
    const result = await processRecipients(to, isDraft, userId);

    if (result.error) {
        return res.status(result.status || 400).json({
            error: `${result.error}${result.invalidEmails?.length ? `: ${result.invalidEmails.join(', ')}` : ''}`
        });
    }

    if (result.error) {
      return res.status(result.status || 400).json({
        error: `${result.error}${result.invalidEmails?.length ? `: ${result.invalidEmails.join(', ')}` : ''}`
      });
    }

    const { validRecipients, responseMeta } = result;
    const mail = await mailService.createMail(userId, validRecipients, subject, body, isDraft);

    if (!mail) {
      return res.status(500).json({ error: 'Failed to create mail' });
    }

    res.status(201).location(`/api/mails/${mail.id}`).json({ id: mail.id, ...responseMeta });
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/:id
 * Returns the full mail content for the requesting user.
 */
async function getMailById(req, res) {
  try {
    const mailId = req.params.id;
    const mail = await mailService.getMailById(mailId, req.user.userId.toLowerCase());
    if (!mail) {
      return res.status(404).json({ error: 'Mail not found' });
    }
    await mailStatusService.markAsRead(mailId, req.user.userId.toLowerCase());
    res.status(200).json(mail);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * DELETE /api/mails/:id
 * Removes the mail from the current user's status map.
 */
async function deleteMail(req, res) {
  try {
    const mailId = req.params.id;
    const success = await mailService.deleteMail(mailId, req.user.userId.toLowerCase());
    if (!success) {
      return res.status(404).json({ error: 'Mail not found' });
    }
    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * PATCH /api/mails/:id
 * Updates the subject, body and recipients of a draft mail.
 */
async function updateMail(req, res) {
  try {
    const mailId = req.params.id;
    const mail = await mailService.getMailById(mailId, req.user.userId.toLowerCase());
    if (!mail) {
      return res.status(404).json({ error: 'Mail not found' });
    }

    const updates = {
      subject: req.body.subject,
      body: req.body.body,
      to: req.body.to
    };

    const result = await mailService.updateMail(mailId, req.user.userId.toLowerCase(), updates);

    if (result === null) {
      return res.status(400).json({ error: 'Only draft mails can be edited' });
    }

    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * PATCH /api/mails/:id/send
 * Sends an existing draft mail to updated recipients.
 */
async function sendDraftMail(req, res) {
  try {
    const mailId = req.params.id;
    const userId = req.user.userId.toLowerCase();

    const mail = await mailService.getMailById(mailId, userId);
    if (!mail) {
      return res.status(404).json({ error: 'Mail not found' });
    }

    const { to = [], subject = '', body = '' } = req.body;
    if (!Array.isArray(to) || to.length === 0) {
      return res.status(400).json({ error: 'Recipients are required for sending mails' });
    }

    const result = processRecipients(to, false, res, userId);

    if (result.error) {
      await mailService.deleteMail(mailId, userId);
      return res.status(result.status || 400).json({
        error: `${result.error}${result.invalidEmails?.length ? `: ${result.invalidEmails.join(', ')}` : ''}`
      });
    }

    const { validRecipients, responseMeta } = result;

    const updates = { subject, body, to: validRecipients };
    const updated = await mailService.sendDraft(mailId, userId, updates);

    if (updated === null) {
      return res.status(400).json({ error: 'Only draft mails can be sent' });
    }

    return res.status(200).json({
      message: 'Mail sent successfully',
      mailId,
      ...responseMeta
    });
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/search-:query
 * Searches accessible mails by query string in subject/body.
 */
async function searchMails(req, res) {
  try {
    let query = req.params.query?.trim();
    if (query.startsWith('search-')) {
      query = query.replace('search-', '');
    }

    const { limit = 5, offset = 0 } = req.query;
    if (!query) {
      return res.status(400).json({ error: 'Search query cannot be empty' });
    }

    const results = await mailService.searchMails(req.user.userId.toLowerCase(), query, +limit, +offset);
    res.status(200).json(results);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * POST /api/mails/:id/labels
 * Adds a label to a mail for the current user.
 */
async function addLabelToMail(req, res) {
  try {
    const mailId = req.params.id;
    const labelId = req.body.labelId;
    const userId = req.user.userId.toLowerCase();

    if (!labelId) {
      return res.status(400).json({ error: 'labelId is required' });
    }

    if (!labelExistsForUser(userId, labelId)) {
      return res.status(404).json({ error: 'Label not found' });
    }

    const result = await mailStatusService.addLabel(mailId, userId, labelId);

    if (result === null) {
      return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === -1) {
      return res.status(400).json({ error: 'Cannot add label to a spam mail' });
    }

    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * DELETE /api/mails/:id/labels
 * Removes a label from a mail for the current user.
 */
async function removeLabelFromMail(req, res) {
  try {
    const mailId = req.params.id;
    const labelId = req.body.labelId;
    const userId = req.user.userId.toLowerCase();

    if (!labelId) {
      return res.status(400).json({ error: 'labelId is required' });
    }

    if (!labelExistsForUser(userId, labelId)) {
      return res.status(404).json({ error: 'Label not found' });
    }

    const result = await mailStatusService.removeLabel(mailId, userId, labelId);

    if (result === null) {
      return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === -1) {
      return res.status(400).json({ error: 'Cannot remove label from a spam mail' });
    }

    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * POST /api/mails/:id/star
 * Toggles the "starred" status of a mail.
 */
async function toggleStar(req, res) {
  try {
    const mailId = req.params.id;
    const userId = req.user.userId.toLowerCase();

    const status = await mailStatusService.getMailStatus(mailId, userId);
    if (!status) {
      return res.status(404).json({ error: 'Mail not found' });
    }

    if (status.isSpam) {
      return res.status(400).json({ error: 'Cannot star a spam mail' });
    }

    await mailStatusService.toggleStar(mailId, userId);
    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * PATCH /api/mails/:id/spam
 * Updates the spam status of a mail for the current user.
 */
async function setSpamStatus(req, res) {
  try {
    const mailId = req.params.id;
    const userId = req.user.userId.toLowerCase();
    const { isSpam } = req.body;

    if (typeof isSpam !== 'boolean') {
      return res.status(400).json({ error: 'isSpam must be true or false' });
    }

    const mail = await mailService.getMailById(mailId, userId);
    if (!mail) {
      return res.status(404).json({ error: 'Mail not found' });
    }

    const result = await mailStatusService.setSpamStatus(mailId, userId, isSpam);
    if (result === null) {
      return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === false) {
      return res.status(403).json({ error: 'Only recipients can mark mail as spam' });
    }

    const urls = (mail.subject + ' ' + mail.body).split(/\s+/);

    if (isSpam) {
      await addUrlsToBlacklist(urls);
    } else {
      await removeUrlsFromBlacklist(urls);
    }

    res.status(204).end();
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/inbox
 * Returns the current user's non-spam received mails.
 * Supports pagination via limit and offset.
 */
async function getInboxMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getInboxMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/sent
 * Returns non-draft mails sent by the current user.
 * Supports pagination via limit and offset.
 */
async function getSentMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getSentMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/spam
 * Returns mails marked as spam by the user.
 * Supports pagination via limit and offset.
 */
async function getSpamMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getSpamMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/drafts
 * Returns draft mails created by the current user.
 * Supports pagination via limit and offset.
 */
async function getDraftMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getDraftMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/starred
 * Returns all non-spam starred mails for the user.
 * Supports pagination via limit and offset.
 */
async function getStarredMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getStarredMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/allmails
 * Returns all non-spam mails for the current user.
 * Supports pagination via limit and offset.
 */
async function getAllMails(req, res) {
  try {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getAllNonSpamMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

/**
 * GET /api/mails/labels/:labelId
 * Returns all non-spam mails associated with a label.
 * Returns:
 *   - 200 with list of mail summaries
 *   - 404 if label does not exist
 */
async function getMailsByLabel(req, res) {
  try {
    let { labelId } = req.params;
    const { limit = 50, offset = 0 } = req.query;
    const userId = req.user.userId.toLowerCase();

    if (labelId.startsWith('labels-')) {
      labelId = labelId.replace('labels-', '');
    }

    if (!labelExistsForUser(userId, labelId)) {
      return res.status(404).json({ error: 'Label not found' });
    }

    const mails = await mailStatusService.getMailsByLabel(userId, labelId, +limit, +offset);
    res.status(200).json(mails);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
}

module.exports = {
    createMail,
    getMailById,
    updateMail,
    deleteMail,
    searchMails,
    getInboxMails,
    getSentMails,
    getStarredMails,
    getAllMails,
    getSpamMails,
    getDraftMails,
    sendDraftMail,
    toggleStar,
    getMailsByLabel,
    addLabelToMail,
    removeLabelFromMail,
    setSpamStatus
};
