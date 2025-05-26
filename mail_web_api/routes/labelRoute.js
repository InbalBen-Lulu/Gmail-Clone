const express = require('express');
const router = express.Router();

const labelController = require('../controllers/labelController');
const validateUserHeader = require('../middleware/validateUser');

// Apply user validation middleware to all label routes
router.use(validateUserHeader);

// Routes for /api/labels
router.route('/')
    .get(labelController.getAllLabels)
    .post(labelController.createLabel);

// Routes for /api/labels/:id
router.route('/:id')
    .get(labelController.getLabelById)
    .patch(labelController.renameLabel)
    .delete(labelController.deleteLabel);

module.exports = router;
