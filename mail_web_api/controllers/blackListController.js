const { addToBlacklist, removeFromBlacklist } = require('../models/blackListModel');

/**
 * Adds a URL to the blacklist.
 * Expected input: { url: "http://example.com" } in req.body
 */
async function addUrlToBlacklist(req, res) {
    const url = req.body.url;
    if (!url) {
        return res.status(400).json({ error: 'Missing url in request body' });
    }

    try {
        const result = await addToBlacklist(url);
        if (result.success) {
            res.status(201).json({ message: result.message }); // 201 Created
        } else {
            res.status(500).json({ error: result.message }); // Failed to add URL (no response)
        }
    } catch (err) {
        res.status(500).json({ error: 'Server error' });
    }
}

/**
 * Removes a URL from the blacklist using :id param.
 * Expects the id (URL) in the request path.
 */
async function removeUrlFromBlacklist(req, res) {
    const url = req.params.id;

    try {
        const result = await removeFromBlacklist(url);

        if (result.success) {
            res.status(204).send(); // No content
        } else if (result.message.includes('not found')) { 
            res.status(404).json({ error: result.message }); // URL not found in blacklist
        } else if (result.message.includes('Invalid')) { 
            res.status(400).json({ error: result.message }); // Invalid DELETE request
        } else { 
            res.status(500).json({ error: result.message }); // Unexpected response
        }
    } catch (err) {
        res.status(500).json({ error: 'Server error' });
    }
}

module.exports = {
    addUrlToBlacklist,
    removeUrlFromBlacklist
};
