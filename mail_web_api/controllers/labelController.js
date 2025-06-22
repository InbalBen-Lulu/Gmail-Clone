const labelModel = require('../models/labelModel');

/**
 * GET /api/labels
 * Returns all labels for the authenticated user.
 */
function getAllLabels(req, res) {
    const labels = labelModel.getLabelsByUser(req.user.userId.toLowerCase());
    res.status(200).json(labels);
}

/**
 * POST /api/labels
 * Creates a new label for the user.
 * - Returns 400 if name is missing or already exists for the user.
 * - Returns 201 with Location header if created successfully.
 */
function createLabel(req, res) {
    const { name } = req.body;

    if (!name) {
        return res.status(400).json({ error: 'Name is required' });
    }

    const result = labelModel.createLabel(name, req.user.userId.toLowerCase());
    if (result === null) {
        return res.status(400).json({ error: 'Label with the same name already exists' });
    }

    res.status(201).location(`/api/labels/${result.id}`).end();
}

/**
 * GET /api/labels/:id
 * Retrieves a single label by its ID.
 * - Returns 404 if the ID is invalid or not found for the user.
 */
function getLabelById(req, res) {
    const labelId = parseInt(req.params.id);
    if (isNaN(labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const label = labelModel.getLabelById(req.user.userId.toLowerCase(), labelId);
    if (!label) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(200).json(label);
}

/**
 * PATCH /api/labels/:id
 * Renames a label.
 * - Returns 404 if label is not found or ID is invalid.
 * - Returns 400 if the new name already exists for this user.
 * - Returns 204 on success.
 */
function renameLabel(req, res) {
    const labelId = parseInt(req.params.id);
    const { name } = req.body;

    if (isNaN(labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    if (!name) {
        return res.status(400).json({ error: 'Name is required' });
    }

    const result = labelModel.renameLabel(req.user.userId.toLowerCase(), labelId, name);
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
 * - Returns 404 if label is not found or ID is invalid.
 * - Returns 204 on successful deletion.
 */
function deleteLabel(req, res) {
    const labelId = parseInt(req.params.id);
    if (isNaN(labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const result = labelModel.deleteLabel(req.user.userId.toLowerCase(), labelId);
    if (result === -1) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(204).end();
}

/**
 * PATCH /api/labels/:id/color
 * Updates the color of a label.
 * - Returns 404 if label is not found or ID is invalid.
 * - Returns 400 if the provided color is invalid.
 * - Returns 204 on success.
 */
function setLabelColor(req, res) {
    const labelId = parseInt(req.params.id);
    const { color } = req.body;

    if (isNaN(labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const result = labelModel.setLabelColor(req.user.userId.toLowerCase(), labelId, color);

    if (result === false) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(204).end();
}

/**
 * DELETE /api/labels/:id/color
 * Resets the color of a label to the default value.
 * - Returns 404 if label is not found or ID is invalid.
 * - Returns 204 on success.
 */
function resetLabelColor(req, res) {
    const labelId = parseInt(req.params.id);

    if (isNaN(labelId)) {
        return res.status(404).json({ error: 'Label not found' });
    }

    const result = labelModel.resetLabelColor(req.user.userId.toLowerCase(), labelId);

    if (result === false) {
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
