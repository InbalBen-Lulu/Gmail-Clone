import { useCallback } from 'react';
import useAuthFetch from '../hooks/useAuthFetch';

/**
 * Provides mail-related API operations: sending, fetching, searching, etc.
 */
export const useMailService = () => {
  const authFetch = useAuthFetch();

  /**
   * Fetches mails by category with pagination.
   * Expected server response: { total: number, mails: [...] }
   */
  const fetchMails = async (offset, limit, category) => {
    const url = `/api/mails/${category}?offset=${offset}&limit=${limit}`;
    const response = await authFetch(url);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to fetch mails');
    }

    return await response.json(); // { total, mails }
  };

  /**
   * Sends a new mail or an existing draft.
   */
  const sendMail = async ({ id, to, subject, body, isDraft }) => {
    let response;

    if (isDraft && id) {
      response = await authFetch(`/api/mails/${id}/send`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ to, subject, body })
      });
    } else {
      response = await authFetch('/api/mails', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ to, subject, body, isDraft: false })
      });
    }

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to send mail');
    }

    return await response.json(); // { id, ...responseMeta }
  };

  /**
   * Saves a mail as a draft.
   */
  const saveDraft = async ({ to, subject, body }) => {
    const response = await authFetch('/api/mails', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ to, subject, body, isDraft: true })
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to save draft');
    }

    return await response.json(); // { id, ...responseMeta }
  };

  /**
   * Fetches a mail by ID.
   */
  const fetchMailById = async (mailId) => {
    const response = await authFetch(`/api/mails/${mailId}`);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to fetch mail');
    }

    return await response.json(); // full mail object
  };

  /**
   * Deletes a mail by ID.
   */
  const deleteMail = async (mailId) => {
    const response = await authFetch(`/api/mails/${mailId}`, {
      method: 'DELETE'
    });

    if (response.status === 204) return;

    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to delete mail');
  };

  /**
   * Toggles the star status of a mail.
   */
  const toggleStar = async (mailId) => {
    const response = await authFetch(`/api/mails/${mailId}/star`, {
      method: 'PATCH'
    });

    if (response.status === 204) return;

    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to toggle star');
  };

  /**
   * Updates the spam status of a mail.
   */
  const setSpamStatus = async (mailId, isSpam) => {
    const response = await authFetch(`/api/mails/${mailId}/spam`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ isSpam })
    });

    if (response.status === 204) return;

    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to update spam status');
  };

  /**
   * Searches mails by a query string (up to 5 results).
   */
  const searchMails = useCallback(async (query) => {
    if (!query?.trim()) return [];

    try {
      const response = await authFetch(`/api/mails/search-${encodeURIComponent(query.trim())}`);
      if (!response.ok) {
        console.warn(`Search failed with status ${response.status}`);
        return [];
      }

      const data = await response.json();
      return data;
    } catch (err) {
      console.error('Search request failed:', err.message || err);
      return [];
    }
  }, [authFetch]);

  return {
    fetchMails,
    sendMail,
    saveDraft,
    fetchMailById,
    deleteMail,
    toggleStar,
    setSpamStatus,
    searchMails
  };
};
