import { useAuth } from '../contexts/AuthContext';
import { getApiUrl, createAuthHeaders } from '../utils/authUtils';

/**
 * Custom hook to send authenticated requests to the server.
 * Automatically includes Authorization header and handles missing or invalid token errors.
 * Note: tokens do not expire automatically, so 401 errors will occur only on manual logout or invalid tokens.
 */
const useAuthFetch = () => {
  const { logout } = useAuth();
  // const navigate = useNavigate();

  /**
   * Sends a secure fetch request with auth headers.
   * Redirects to login if the token is missing or invalid.
   * @param {string} path - Relative API path (e.g. '/users/me')
   * @param {object} options - Fetch options like method, body, headers
   * @returns {Promise<Response>} - Server response
   */
  const authFetch = async (path, options = {}) => {
    const url = getApiUrl(path); // Convert relative path to full API URL

    const headers = {
      ...createAuthHeaders(),
      'Content-Type': 'application/json',
      ...options.headers,
    };

    try {
      const response = await fetch(url, {
        ...options,
        headers,
        credentials: 'include', // Include cookies if any
      });

      // If token is missing or invalid, log out and redirect
      if (response.status === 401) {
        logout();
        // navigate('/login');
        throw new Error('Unauthorized. Please login again.');
      }

      return response;
    } catch (error) {
      throw error;
    }
  };

  return authFetch;
};

export default useAuthFetch;
