/** Returns the full API URL based on environment */
export const getApiUrl = (path) => {
  const base = process.env.REACT_APP_API_BASE_URL || 'http://localhost:3000';
  return `${base}${path}`;
};

/** Creates Authorization headers using token from localStorage */
export const createAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};
