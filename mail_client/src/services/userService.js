import usePublicFetch from '../hooks/usePublicFetch';
import useAuthFetch from '../hooks/useAuthFetch';

/**
 * useUserService provides user-related API actions.
 * This service abstracts the logic for working with the user API.
 */
export const useUserService = () => {
  const publicFetch = usePublicFetch();
  const authFetch = useAuthFetch();

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

  /**
   * Uploads a new profile image (base64 format) to the server for a specific user.
   * Returns the updated image URL.
   */
  const uploadProfileImage = async (userId, imageBase64) => {
    let response;

    try {
      response = await authFetch(`/api/users/${userId}/profile-image`, {
        method: 'POST',
        body: JSON.stringify({ image: imageBase64 })
      });
    } catch (err) {
      throw new Error('Network error while uploading image');
    }

    if (response.status === 413) {
      throw new Error('Image is too large. Please choose a smaller file.');
    }

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || 'Failed to upload image');
    }

    return data.imageUrl;
  };

  /**
   * Removes the user's custom profile image and resets to default.
   * Returns the default image URL.
   */
  const removeProfileImage = async (userId) => {
    let response;

    try {
      response = await authFetch(`/api/users/${userId}/profile-image`, {
        method: 'DELETE'
      });
    } catch (err) {
      throw new Error('Network error while removing image');
    }

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || 'Failed to remove image');
    }

    return data.imageUrl;
  };

  return {
    fetchPublicUser,
    createUser,
    uploadProfileImage,
    removeProfileImage
  };
};
