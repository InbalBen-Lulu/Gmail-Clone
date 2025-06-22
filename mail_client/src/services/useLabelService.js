import useAuthFetch from '../hooks/useAuthFetch';

/**
 * A React hook providing services for label-related API operations.
 * Includes CRUD actions and label-mail assignment.
 */
export const useLabelService = () => {
  const authFetch = useAuthFetch();

  // Adds a label to a specific mail
  const addLabelToMail = async (mailId, labelId) => {
    const response = await authFetch(`/api/mails/${mailId}/labels/`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ labelId })
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Unknown error adding label');
  };

  // Removes a label from a specific mail
  const removeLabelFromMail = async (mailId, labelId) => {
    const response = await authFetch(`/api/mails/${mailId}/labels/`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ labelId })
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Unknown error removing label');
  };

  // Fetches all available labels for the logged-in user
  const fetchLabels = async () => {
    const response = await authFetch('/api/labels');
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to fetch labels');
    }

    const labels = await response.json();
    if (!Array.isArray(labels)) {
      throw new Error('Invalid labels format from server');
    }
    return labels;
  };

  // Creates a new label
  const createLabel = async (name, color) => {
    const response = await authFetch('/api/labels', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, color })
    });

    if (response.status === 201) return;

    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to create label');
  };

  // Gets a single label by its ID
  const getLabelById = async (labelId) => {
    const response = await authFetch(`/api/labels/${labelId}`);
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to fetch label');
    }
    return await response.json();
  };

  // Renames a label
  const renameLabel = async (labelId, newName) => {
    const response = await authFetch(`/api/labels/${labelId}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: newName })
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to rename label');
  };

  // Changes the color of a label
  const setLabelColor = async (labelId, color) => {
    const response = await authFetch(`/api/labels/${labelId}/color`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ color })
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to set label color');
  };

  // Resets the color of a label to default
  const resetLabelColor = async (labelId) => {
    const response = await authFetch(`/api/labels/${labelId}/color`, {
      method: 'DELETE'
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to reset label color');
  };

  // Deletes a label by its ID
  const deleteLabel = async (labelId) => {
    const response = await authFetch(`/api/labels/${labelId}`, {
      method: 'DELETE'
    });

    if (response.status === 204) return;
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to delete label');
  };

  return {
    // Label-Mail actions
    addLabelToMail,
    removeLabelFromMail,

    // General label actions
    fetchLabels,
    createLabel,
    getLabelById,
    renameLabel,
    setLabelColor,
    resetLabelColor,
    deleteLabel
  };
};
