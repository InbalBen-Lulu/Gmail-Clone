const mailService = require('../services/mailService');
const mailStatusService = require('../services/mailStatusService');
const { labelExistsForUser } = require('../services/labelService');
const { addUrlsToBlacklist, removeUrlsFromBlacklist } = require('../models/blackListModel');
const { processRecipients } = require('../utils/mailUtils');

/**
 * POST /api/mails
 * Creates a new mail and sends it to recipients.
 * Returns:
 *   - 201 with mail ID if created successfully
 *   - 400/500 with appropriate error message on failure
 */
async function createMail(req, res) {
    const { to = [], subject = '', body = '', isDraft = false } = req.body;

    if (!isDraft && (!Array.isArray(to) || to.length === 0)) {
        return res.status(400).json({ error: 'Recipients are required for sending mails' });
    }

    const userId = req.user.userId.toLowerCase();

    const result = processRecipients(to, isDraft, res, userId);

    if (!result) {
        return res.status(500).json({ error: 'Unexpected error occurred while creating mail' });
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
}

/**
 * GET /api/mails/:id
 * Returns the full mail content for the requesting user.
 * Marks the mail as read.
 * Returns:
 *   - 200 with mail content
 *   - 404 if not found or inaccessible
 */
async function getMailById(req, res) {
    const mailId = req.params.id;
    const mail = await mailService.getMailById(mailId, req.user.userId.toLowerCase());
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    await mailStatusService.markAsRead(mailId, req.user.userId.toLowerCase());
    res.status(200).json(mail);
}

/**
 * DELETE /api/mails/:id
 * Removes the mail from the current user's status map.
 * If it was a draft, deletes the mail completely.
 * Returns:
 *   - 204 on success
 *   - 404 if mail not found or already deleted
 */
async function deleteMail(req, res) {
    const mailId = req.params.id;

    const success = await mailService.deleteMail(mailId, req.user.userId.toLowerCase());
    if (!success) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    res.status(204).end();
}

/**
 * PATCH /api/mails/:id
 * Updates the subject, body and recipients of a draft mail.
 * Only the sender can perform this action.
 * Returns:
 *   - 204 if update succeeded
 *   - 400 if not a draft or invalid input
 *   - 404 if mail not found
 */
async function updateMail(req, res) {
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
}

/**
 * PATCH /api/mails/:id/send
 * Sends an existing draft mail to updated recipients.
 * If no valid recipients are found, deletes the draft mail.
 * Returns:
 *   - 200 if mail sent successfully
 *   - 400/404 if mail is not valid or not a draft
 */
async function sendDraftMail(req, res) {
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

    const updates = {
        subject,
        body,
        to: validRecipients
    };

    const updated = await mailService.sendDraft(mailId, userId, updates);
    if (updated === null) {
        return res.status(400).json({ error: 'Only draft mails can be sent' });
    }

    return res.status(200).json({
        message: 'Mail sent successfully',
        mailId,
        ...responseMeta 
    });
}

/**
 * GET /api/mails/search-:query
 * Searches accessible mails by query string in subject/body.
 * Returns:
 *   - 200 with array of mail summaries
 *   - 400 if query is empty
 */
async function searchMails(req, res) {
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
}

/**
 * POST /api/mails/:id/labels
 * Adds a label to a mail for the current user.
 * Returns:
 *   - 204 on success
 *   - 400 if label is missing or mail is spam
 *   - 404 if mail or label not found
 */
async function addLabelToMail(req, res) {
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
}

/**
 * DELETE /api/mails/:id/labels
 * Removes a label from a mail for the current user.
 * Returns:
 *   - 204 on success
 *   - 400 if label is missing or mail is spam
 *   - 404 if mail or label not found
 */
async function removeLabelFromMail(req, res) {
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
}

/**
 * POST /api/mails/:id/star
 * Toggles the "starred" status of a mail.
 * Only works on non-spam mails.
 * Returns:
 *   - 204 on success
 *   - 400 if mail is spam or ID is invalid
 *   - 404 if mail not found
 */
async function toggleStar(req, res) {
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
}

/**
 * PATCH /api/mails/:id/spam
 * Updates the spam status of a mail for the current user.
 * Also updates blacklist if necessary.
 * Returns:
 *   - 204 on success
 *   - 400 if isSpam is invalid
 *   - 403 if user is not allowed to mark this mail as spam
 *   - 404 if mail not found
 */
async function setSpamStatus(req, res) {
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
}

/**
 * GET /api/mails/inbox
 * Returns the current user's non-spam received mails.
 * Supports pagination via limit and offset.
 */
async function getInboxMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getInboxMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/sent
 * Returns non-draft mails sent by the current user.
 * Supports pagination via limit and offset.
 */
async function getSentMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getSentMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/spam
 * Returns mails marked as spam by the user.
 * Supports pagination via limit and offset.
 */
async function getSpamMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getSpamMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/drafts
 * Returns draft mails created by the current user.
 * Supports pagination via limit and offset.
 */
async function getDraftMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getDraftMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/starred
 * Returns all non-spam starred mails for the user.
 * Supports pagination via limit and offset.
 */
async function getStarredMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getStarredMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/allmails
 * Returns all non-spam mails for the current user.
 * Supports pagination via limit and offset.
 */
async function getAllMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = await mailStatusService.getAllNonSpamMails(req.user.userId.toLowerCase(), +limit, +offset);
    res.status(200).json(mails);
}

/**
 * GET /api/mails/labels/:labelId
 * Returns all non-spam mails associated with a label.
 * Returns:
 *   - 200 with list of mail summaries
 *   - 404 if label does not exist
 */
async function getMailsByLabel(req, res) {
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
