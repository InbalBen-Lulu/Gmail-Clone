import usePublicFetch from '../hooks/usePublicFetch';
import useAuthFetch from '../hooks/useAuthFetch';

export const useUserService = () => {
  const publicFetch = usePublicFetch();

  const fetchPublicUser = async (email) => {
    const response = await publicFetch(`/api/users/${email.toLowerCase()}/public`);
    const data = await response.json();
    return { status: response.status, data };
  };

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
