const express = require('express');
const router = express.Router();
const {
  addUrlToBlacklist,
  removeUrlFromBlacklist
} = require('../controllers/blackListController');

// POST /api/blacklist
router.post('/', addUrlToBlacklist);

// DELETE /api/blacklist/:id
router.delete('/:id', removeUrlFromBlacklist);

module.exports = router;
