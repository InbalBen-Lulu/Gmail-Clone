const express = require('express');
const router = express.Router();

const mailController = require('../controllers/mailController');
const { isLoggedIn } = require('../middleware/auth');

// Apply user validation middleware to all mails routes
router.use(isLoggedIn);

router.route('/')
    .get(mailController.getInboxMails)
    .post(mailController.createMail);

router.patch('/:id/send', mailController.sendDraftMail);
router.patch('/:id/star', mailController.toggleStar);
router.patch('/:id/spam', mailController.setSpamStatus);

router.post('/:id/labels', mailController.addLabelToMail);
router.delete('/:id/labels', mailController.removeLabelFromMail);

// Route: Filtered mail views
router.get('/allmail', mailController.getAllMails);
router.get('/inbox', mailController.getInboxMails);
router.get('/sent', mailController.getSentMails);
router.get('/drafts', mailController.getDraftMails);
router.get('/spam', mailController.getSpamMails);
router.get('/starred', mailController.getStarredMails);
router.get('/labels-:labelId', mailController.getMailsByLabel);

router.get('/search-:query', mailController.searchMails);

router.route('/:id')
    .get(mailController.getMailById)
    .patch(mailController.updateMail)
    .delete(mailController.deleteMail);

module.exports = router;