const express = require('express');
const router = express.Router();

const mailController = require('../controllers/mailController');
const validateUserHeader = require('../middleware/validateUser');

// Apply user validation middleware to all mails routes
router.use(validateUserHeader);

router.get('/', mailController.getInboxMails);
router.post('/', mailController.createMail);

router.get('/allmails', mailController.getAllMails);
router.get('/inbox', mailController.getInboxMails);
router.get('/sent', mailController.getSentMails);
router.get('/starred', mailController.getStarredMails);
router.get('/search/:query', mailController.searchMails);

router.get('/:id', mailController.getMailById);
router.patch('/:id', mailController.updateMail);
router.delete('/:id', mailController.deleteMail);
router.patch('/:id/star', mailController.toggleStar);

module.exports = router;