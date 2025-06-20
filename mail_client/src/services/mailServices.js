import { useCallback } from 'react';
import useAuthFetch from '../hooks/useAuthFetch';

/**
 * Custom hook to search mails by query string (max 5 results).
 * @returns {function} searchMails(query): Promise<MailSummary[]>
 */
const useMailSearch = () => {
  const authFetch = useAuthFetch();

  const searchMails = useCallback(async (query) => {
    if (!query?.trim()) return [];

    try {
      const response = await authFetch(`/mails/search/${encodeURIComponent(query.trim())}`);
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

  return searchMails;
};

export default useMailSearch;
