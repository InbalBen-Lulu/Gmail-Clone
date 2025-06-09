const mailModel = require('../models/mailModel');
const mailStatusModel = require('../models/mailStatusModel');

/**
 * POST /api/mails
 * Creates a new mail and sends it to recipients.
 * On success, responds with 201 and sets the Location header to the new mail's URL.
 */
async function createMail(req, res) {
    const { to = [], subject = '', body = '', isDraft = false } = req.body;

    if (!isDraft && (!Array.isArray(to) || to.length === 0)) {
        return res.status(400).json({ error: 'Recipients are required for sending mails' });
    }

    const mail = await mailModel.createMail(req.userId, to, subject, body, isDraft);

    if (!mail) {
        return res.status(500).json({ error: 'Failed to create mail' });
    }

    res.status(201).location(`/api/mails/${mail.id}`).json({ id: mail.id });
}

/**
 * GET /api/mails/:id
 * Returns a mail if the user has access to it.
 */
function getMailById(req, res) {
    const mailId = parseInt(req.params.id);
    const mail = mailModel.getMailById(mailId, req.userId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    res.status(200).json(mail);
}

/**
 * DELETE /api/mails/:id
 * Removes the mail only from the user's list.
 */
function deleteMail(req, res) {
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const success = mailModel.deleteMail(mailId, req.userId);
    if (!success) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    res.status(204).end();
}

/**
 * PATCH /api/mails/:id
 * Updates subject/body of a mail.
 * Only the sender can update.
 * Handles blacklist violations.
 */
async function updateMail(req, res) {
    const mailId = parseInt(req.params.id);
    const original = mailModel.getMailById(mailId, req.userId);
    if (!original) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    if ('from' in req.body || 'to' in req.body) {
        return res.status(400).json({ error: 'You cannot modify this field' });
    }

    const allowedFields = ['subject', 'body'];
    const updates = {};
    for (const field of allowedFields) {
        if (field in req.body) {
            updates[field] = req.body[field];
        }
    }

    const result = await mailModel.updateMail(mailId, req.userId, updates);

    if (result === null) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === -1) {
        return res.status(400).json({ error: 'Mail contains a blacklisted link' });
    }

    res.status(204).end();
}

/**
 * GET /api/mails/search/:query
 * Searches mails accessible to the user.
 */
function searchMails(req, res) {
    const query = req.params.query?.trim();

    // query not sent at all
    if (!query) {
        return res.status(400).json({ error: 'Search query cannot be empty' });
    }

    const results = mailModel.searchMails(req.userId, query);
    res.status(200).json(results);
}

function getInboxMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getInboxMails(req.userId, +limit, +offset);
    res.status(200).json(mails);
}

function getSentMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getSentMails(req.userId, +limit, +offset);
    res.status(200).json(mails);
}

function getStarredMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getStarredMails(req.userId, +limit, +offset);
    res.status(200).json(mails);
}

function getAllMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getAllNonSpamMails(req.userId, +limit, +offset);
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
    getAllMails
};
