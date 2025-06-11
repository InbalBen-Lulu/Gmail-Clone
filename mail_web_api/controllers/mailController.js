const mailModel = require('../models/mailModel');
const mailStatusModel = require('../models/mailStatusModel');
const { labelExistsForUser } = require('../models/userLabelsModel');
const { extractUrls } = require('../utils/urlUtils');
const { addUrlsToBlacklist, removeUrlsFromBlacklist } = require('../models/blackListModel');
const { getUserByEmail } = require('../models/userModel');

function processRecipients(to, isDraft, res) {
    const validRecipients = [];
    const invalidRecipients = [];

    for (const email of to) {
        const user = getUserByEmail(email);
        if (user) validRecipients.push(user.id);
        else invalidRecipients.push(email);
    }

    if (!isDraft && validRecipients.length === 0) {
        res.status(400).json({
            error: 'Mail was not sent. None of the recipients exist.',
            invalidEmails: invalidRecipients
        });
        return null;
    }

    const responseMeta = {};
    if (invalidRecipients.length > 0) {
        responseMeta.warning = 'Mail was not sent to some addresses because they do not exist';
        responseMeta.invalidEmails = invalidRecipients;
    }

    return { validRecipients, responseMeta };
}

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

    const result = processRecipients(to, isDraft, res);
    if (!result) return;

    const { validRecipients, responseMeta } = result;

    const mail = await mailModel.createMail(req.userId, validRecipients, subject, body, isDraft);
    if (!mail) {
        return res.status(500).json({ error: 'Failed to create mail' });
    }

    res.status(201).location(`/api/mails/${mail.id}`).json({ id: mail.id, ...responseMeta });
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
    mailStatusModel.markAsRead(mailId, req.userId);
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
 */
function updateMail(req, res) {
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    const mail = mailModel.getMailById(mailId, req.userId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const updates = {
        subject: req.body.subject,
        body: req.body.body,
        to: req.body.to
    };

    const result = mailModel.updateMail(mailId, req.userId, updates);

    if (result === null) {
        return res.status(400).json({ error: 'Only draft mails can be edited' });
    }

    res.status(204).end();
}

async function sendDraftMail(req, res) {
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    const mail = mailModel.getMailById(mailId, req.userId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const { to = [], subject = '', body = '' } = req.body;

    if (!Array.isArray(to) || to.length === 0) {
        return res.status(400).json({ error: 'Recipients are required for sending mails' });
    }

    const result = processRecipients(to, false, res);
    if (!result) {
        mailModel.deleteMail(mailId, req.userId);
        return;
    }

    const { validRecipients, responseMeta } = result;

    const updates = {
        subject,
        body,
        to: validRecipients  
    };

    const updated = await mailModel.sendDraft(mailId, req.userId, updates);
    if (updated === null) {
        return res.status(400).json({ error: 'Only draft mails can be sent' });
    }

    res.status(200).json({ message: 'Mail sent successfully', mailId, ...responseMeta });
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

function addLabelToMail(req, res) {
    const mailId = parseInt(req.params.id);
    const labelId = req.body.labelId;

    if (!labelId) {
        return res.status(400).json({ error: 'labelId is required' });
    }

    if (!labelExistsForUser(req.userId, labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const result = mailStatusModel.addLabel(mailId, req.userId, labelId);

    if (result === null) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === -1) {
        return res.status(400).json({ error: 'Cannot add label to a spam mail' });
    }

    res.status(204).end();
}

function removeLabelFromMail(req, res) {
    const mailId = parseInt(req.params.id);
    const labelId = req.body.labelId;

    if (!labelId) {
        return res.status(400).json({ error: 'labelId is required' });
    }

    if (!labelExistsForUser(req.userId, labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const result = mailStatusModel.removeLabel(mailId, req.userId, labelId);

    if (result === null) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (result === -1) {
        return res.status(400).json({ error: 'Cannot remove label from a spam mail' });
    }

    res.status(204).end();
}

function toggleStar(req, res) {
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    const status = mailStatusModel.getMailStatus(mailId, req.userId);
    if (!status) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    if (status.isSpam) {
        return res.status(400).json({ error: 'Cannot star a spam mail' });
    }

    mailStatusModel.toggleStar(mailId, req.userId);
    res.status(204).end();
}

async function setSpamStatus(req, res) {
    const mailId = parseInt(req.params.id);
    const { isSpam } = req.body;

    if (typeof isSpam !== 'boolean') {
        return res.status(400).json({ error: 'isSpam must be true or false' });
    }

    const mail = mailModel.getMailById(mailId, req.userId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const success = mailStatusModel.setSpamStatus(mailId, req.userId, isSpam);
    if (!success) {
        return res.status(500).json({ error: 'Failed to update spam status' });
    }

    const urls = extractUrls(mail.subject + ' ' + mail.body);

    if (isSpam) {
        await addUrlsToBlacklist(urls);
    } else {
        await removeUrlsFromBlacklist(urls);
    }

    res.status(204).end();
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

function getSpamMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getSpamMails(req.userId, +limit, +offset);
    res.status(200).json(mails);
}

function getDraftMails(req, res) {
    const { limit = 50, offset = 0 } = req.query;
    const mails = mailStatusModel.getDraftMails(req.userId, +limit, +offset);
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

function getMailsByLabel(req, res) {
    const { labelId } = req.params;
    const { limit = 50, offset = 0 } = req.query;

    if (!labelExistsForUser(req.userId, labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const mails = mailStatusModel.getMailsByLabel(req.userId, labelId, +limit, +offset);
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
