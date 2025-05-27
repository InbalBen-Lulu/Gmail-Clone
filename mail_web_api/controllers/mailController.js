const mailModel = require('../models/mailModel');
const { getUserByEmail } = require('../models/userModel');

/**
 * POST /api/mails
 * Creates a new mail and sends it to recipients.
 */
function createMail(req, res) {
    const { to, subject = '', body = '' } = req.body;

    if (!Array.isArray(to) || to.length === 0) {
        return res.status(400).json({ error: 'Recipients are required' });
    }

    const resolvedIds = [];

    for (const email of to) {
        const user = getUserByEmail(email);
        if (!user) {
            return res.status(404).json({ error: `Recipient not found: ${email}` });
        }
        resolvedIds.push(user.userId);
    }

    const result = mailModel.createMail(req.userId, resolvedIds, subject, body);

    if (result === null) {
        return res.status(400).json({ error: 'Mail contains a blacklisted link' });
    }

    res.status(201).json(result);
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
 * GET /api/mails
 * Returns up to `limit` mails for the user.
 */
function getMailsForUser(req, res) {
    const limit = parseInt(req.query.limit) || 50;
    const mails = mailModel.getMailsForUser(req.userId, limit);
    res.status(200).json(mails);
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
 * Allows editing subject/body only, with blacklist check.
 */
function updateMail(req, res) {
    const mailId = parseInt(req.params.id);

    // Always treat non-numeric or missing ID as not found
    const original = mailModel.getMailById(mailId, req.userId);
    if (!original) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    // Do not allow changing sender or recipients
    if ('from' in req.body || 'to' in req.body) {
        return res.status(400).json({ error: 'You cannot modify this field' });
    }

    // Only allow updating subject and body
    const allowedFields = ['subject', 'body'];
    const updates = {};

    for (const field of allowedFields) {
        if (field in req.body) {
            updates[field] = req.body[field];
        }
    }

    const success = mailModel.updateMail(mailId, req.userId, updates);

    if (!success) {
        return res.status(400).json({ error: 'Mail contains a blacklisted link' });
    }

    res.status(204).end();
}


/**
 * GET /api/mails/search/:query
 * Searches mails accessible to the user.
 */
function searchMails(req, res) {
    const query = req.params.query;

    // query not sent at all
    if (query === undefined || query === null) {
        return res.status(400).json({ error: 'Search query is required' });
    }

    const results = mailModel.searchMails(req.userId, query);
    res.status(200).json(results);
}

module.exports = {
    createMail,
    getMailById,
    getMailsForUser,
    deleteMail,
    updateMail,
    searchMails
};
