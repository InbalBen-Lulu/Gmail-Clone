const labelModel = require('../services/labelService');

/**
 * GET /api/labels
 * Returns all labels for the authenticated user.
 */
async function getAllLabels(req, res) {
    const labels = await labelModel.getLabelsByUser(req.user.userId);
    res.status(200).json(labels);
}

/**
 * POST /api/labels
 * Creates a new label for the user.
 */
async function createLabel(req, res) {
    const { name } = req.body;

    if (!name) {
        return res.status(400).json({ error: 'Name is required' });
    }

    const result = await labelModel.createLabel(name, req.user.userId);
    if (!result) {
        return res.status(400).json({ error: 'Label with the same name already exists' });
    }

    res.status(201).location(`/api/labels/${result.id}`).end();
}

/**
 * GET /api/labels/:id
 * Retrieves a label by ID.
 */
async function getLabelById(req, res) {
    const { id } = req.params;
    const label = await labelModel.getLabelById(req.user.userId, id);
    if (!label) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(200).json(label);
}

/**
 * PATCH /api/labels/:id
 * Renames a label.
 */
async function renameLabel(req, res) {
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
}

/**
 * DELETE /api/labels/:id
 * Deletes a label by its ID.
 */
async function deleteLabel(req, res) {
    const { id } = req.params;
    const result = await labelModel.deleteLabel(req.user.userId, id);
    if (result === -1) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(204).end();
}

/**
 * PATCH /api/labels/:id/color
 * Updates label color.
 */
async function setLabelColor(req, res) {
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
}

/**
 * DELETE /api/labels/:id/color
 * Resets label color.
 */
async function resetLabelColor(req, res) {
    const { id } = req.params;
    const result = await labelModel.resetLabelColor(req.user.userId, id);
    if (!result) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(204).end();
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
