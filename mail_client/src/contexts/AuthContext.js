import { createContext, useState, useContext, useCallback, useEffect } from 'react';

// Create the auth context to share login/logout state across the app
const AuthContext = createContext(null);

/**
 * AuthProvider component.
 * Wraps the app and provides auth state and actions (login/logout).
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true); // true until auth check finishes

  /**
   * Logs out the currently authenticated user.
   * Clears the user's authentication cookie and removes user data from localStorage.
   * Sends a request to the server to log out and clear the session.
   */
  const logout = useCallback(async () => {
    try {
      // Send a request to the server to log out and clear the cookie
      await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/tokens/logout`, {
        method: 'POST',
        credentials: 'include'  // Include cookies with the request
      });

      // Remove the token and user data from localStorage
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // Reset the user state to null
      setUser(null);
    } catch (error) {
      console.error('Logout failed', error);
      throw new Error('Logout failed. Please try again.');
    }
  }, []);


  // On first load – try to restore auth state from localStorage
  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (token && storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (error) {
        console.error('Error parsing stored user:', error);
        logout();
      }
    }
    setIsLoading(false);
  }, []);

  // Log in the user – sends credentials, receives token+user
  const login = useCallback(async (userId, password) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/tokens/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId, password }),
        credentials: 'include'
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Login failed');
      }

      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      setUser(data.user);

      return data.user;
    } catch (error) {
      throw new Error(error.message || 'Login failed');
    }
  }, []);

  // Build headers for authenticated API requests
  const getAuthHeaders = useCallback(() => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }, []);

  const value = {
    user,
    setUser,
    isLoading,
    isAuthenticated: !!user,
    isAdmin: user?.isAdmin || false,
    login,
    logout,
    getAuthHeaders
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

/**
 * useAuth hook.
 * Allows components to use the auth context.
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
