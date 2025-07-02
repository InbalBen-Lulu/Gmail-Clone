const labelModel = require('../services/labelService');

/**
 * GET /api/labels
 * Returns all labels for the authenticated user.
 */
async function getAllLabels(req, res) {
    try {
        const labels = await labelModel.getLabelsByUser(req.user.userId);
        res.status(200).json(labels);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch labels' });
    }
}

/**
 * POST /api/labels
 * Creates a new label for the user.
 */
async function createLabel(req, res) {
    try {
        const { name } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Name is required' });
        }

        const result = await labelModel.createLabel(name, req.user.userId);
        if (!result) {
            return res.status(400).json({ error: 'Label with the same name already exists' });
        }

        res.status(201).location(`/api/labels/${result.id}`).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to create label' });
    }
}

/**
 * GET /api/labels/:id
 * Retrieves a label by ID.
 */
async function getLabelById(req, res) {
    try {
        const { id } = req.params;
        const label = await labelModel.getLabelById(req.user.userId, id);
        if (!label) {
            return res.status(404).json({ error: 'Label not found' });
        }

        res.status(200).json(label);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch label' });
    }
}

/**
 * PATCH /api/labels/:id
 * Renames a label.
 */
async function renameLabel(req, res) {
    try {
        const { id } = req.params;
        const { name } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Name is required' });
        }

        const result = await labelModel.renameLabel(req.user.userId, id, name);
        if (result === -1) {
            return res.status(404).json({ error: 'Label not found' });
        }
        if (result === null) {
            return res.status(400).json({ error: 'Another label with this name already exists' });
        }

        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to rename label' });
    }
}

/**
 * DELETE /api/labels/:id
 * Deletes a label by its ID.
 */
async function deleteLabel(req, res) {
    try {
        const { id } = req.params;
        const result = await labelModel.deleteLabel(req.user.userId, id);
        if (result === -1) {
            return res.status(404).json({ error: 'Label not found' });
        }

        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to delete label' });
    }
}

/**
 * PATCH /api/labels/:id/color
 * Updates label color.
 */
async function setLabelColor(req, res) {
    try {
        const { id } = req.params;
        const { color } = req.body;

        if (!color) {
            return res.status(400).json({ error: 'Color is required' });
        }

        const result = await labelModel.setLabelColor(req.user.userId, id, color);
        if (!result) {
            return res.status(404).json({ error: 'Label not found' });
        }

        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to update label color' });
    }
}

/**
 * DELETE /api/labels/:id/color
 * Resets label color.
 */
async function resetLabelColor(req, res) {
    try {
        const { id } = req.params;
        const result = await labelModel.resetLabelColor(req.user.userId, id);
        if (!result) {
            return res.status(404).json({ error: 'Label not found' });
        }

        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to reset label color' });
    }
}

module.exports = {
    getAllLabels,
    createLabel,
    getLabelById,
    renameLabel,
    deleteLabel,
    setLabelColor,
    resetLabelColor
};
