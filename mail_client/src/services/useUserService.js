import usePublicFetch from '../hooks/usePublicFetch';

/**
 * useUserService provides user-related API actions.
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
   * Returns an object with status and data or throws error if response not ok.
   */
  const createUser = async ({ userId, name, password, gender, birthDate }) => {
    const response = await publicFetch('/api/users', {
      method: 'POST',
      body: JSON.stringify({ userId, name, password, gender, birthDate })
    });

    const data = await response.json();

    if (!response.ok) {
      const error = new Error(data.error || 'Failed to create user');
      error.status = response.status;
      throw error;
    }

    return { status: response.status, data };
  };

  return { fetchPublicUser, createUser };
};
