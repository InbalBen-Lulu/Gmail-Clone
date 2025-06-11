const express = require('express');
const router = express.Router();

const labelController = require('../controllers/labelController');
const { isLoggedIn } = require('../middleware/auth');

// Apply user validation middleware to all label routes
router.use(isLoggedIn);

// Routes for /api/labels
router.route('/')
    .get(labelController.getAllLabels)
    .post(labelController.createLabel);

// Routes for /api/labels/:id/color    
router.route('/:id/color')
    .patch(labelController.setLabelColor)
    .delete(labelController.resetLabelColor);
    
// Routes for /api/labels/:id
router.route('/:id')
    .get(labelController.getLabelById)
    .patch(labelController.renameLabel)
    .delete(labelController.deleteLabel);

module.exports = router;
