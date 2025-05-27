const express = require('express');
const router = express.Router();

const mailController = require('../controllers/mailController');
const validateUserHeader = require('../middleware/validateUser');

// Apply user validation middleware to all label routes
router.use(validateUserHeader);

// Routes for /api/mails
router.route('/')
    .get(mailController.getMailsForUser)
    .post(mailController.createMail);

// Route for /api/mails/search/:query
router.get('/search/:query', mailController.searchMails);

// Routes for /api/mails/:id
router.route('/:id')
    .get(mailController.getMailById)
    .patch(mailController.updateMail)
    .delete(mailController.deleteMail);

module.exports = router;