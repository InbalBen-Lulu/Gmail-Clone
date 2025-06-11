const express = require('express');
const router = express.Router();

const mailController = require('../controllers/mailController');
const validateUserHeader = require('../middleware/validateUser');

// Apply user validation middleware to all mails routes
router.use(validateUserHeader);

router.route('/')
    .get(mailController.getInboxMails)
    .post(mailController.createMail);

router.patch('/:id/send', mailController.sendDraft);
router.patch('/:id/star', mailController.toggleStar);
router.patch('/:id/spam', mailController.setSpamStatus);

router.patch('/:id/labels/add', mailController.addLabelToMail);
router.patch('/:id/labels/remove', mailController.removeLabelFromMail);

router.route('/:id')
    .get(mailController.getMailById)
    .patch(mailController.updateMail)
    .delete(mailController.deleteMail);

router.get('/search/:query', mailController.searchMails);

router.get('/allmails', mailController.getAllMails);
router.get('/inbox', mailController.getInboxMails);
router.get('/sent', mailController.getSentMails);
router.get('/drafts', mailController.getDraftMails);
router.get('/spam', mailController.getSpamMails);
router.get('/starred', mailController.getStarredMails);
router.get('/labels/:labelId', mailController.getMailsByLabel);

module.exports = router;