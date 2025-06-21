import useAuthFetch from '../hooks/useAuthFetch';

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

  // Fetches all available labels from the server
  const fetchLabels = async () => {
    const response = await authFetch('/api/labels');

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to fetch labels');
    }

    const labels = await response.json();  // array of labels
    if (!Array.isArray(labels)) {
      throw new Error('Invalid labels format from server');
    }

    return labels;
  };

  return {
    addLabelToMail,
    removeLabelFromMail,
    fetchLabels
  };
};
