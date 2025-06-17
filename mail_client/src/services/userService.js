import usePublicFetch from '../hooks/usePublicFetch';
import useAuthFetch from '../hooks/useAuthFetch';

/**
 * useUserService provides user-related API actionsץ
 * This service abstracts the logic for working with the user API.
 */
export const useUserService = () => {
  const publicFetch = usePublicFetch();

  /**
   * Fetches public data for a given email address.
   * Returns an object containing the response status and data.
   */
  const fetchPublicUser = async (email) => {
    const response = await publicFetch(`/api/users/${email.toLowerCase()}/public`);
    const data = await response.json();
    return { status: response.status, data };
  };

  /**
   * Sends a request to create a new user with the provided information.
   * If the request fails, throws an error with the relevant message.
   */
  const createUser = async ({ userId, name, password, gender, birthDate }) => {
    const response = await publicFetch('/api/users', {
      method: 'POST',
      body: JSON.stringify({ userId, name, password, gender, birthDate })
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Failed to create user');
    }
  };

  return { fetchPublicUser, createUser };
};
