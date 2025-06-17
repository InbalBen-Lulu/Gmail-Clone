import { createContext, useState, useContext, useCallback, useEffect } from 'react';

// Create an authentication context
const AuthContext = createContext(null);

// AuthProvider wraps the app and provides authentication state and methods
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);         // Current user object
  const [isLoading, setIsLoading] = useState(true); // Whether auth is initializing

  // On initial load: try to load auth state from localStorage
  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (token && storedUser) {
      try {
        setUser(JSON.parse(storedUser));          // Parse user JSON
      } catch (error) {
        console.error('Error parsing stored user:', error);
        logout();                                  // Clear storage on error
      }
    }
    setIsLoading(false);                           // Initialization done
  }, []);

  // Login function: send credentials, store token & user if successful
  const login = useCallback(async (userId, password) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/tokens/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Login failed');
      }

      // Save auth data to localStorage
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      setUser(data.user);

      return data.user;
    } catch (error) {
      throw new Error(error.message || 'Login failed');
    }
  }, []);

  // Logout function: clear auth data
  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }, []);

  // Helper to get auth headers for API calls
  const getAuthHeaders = useCallback(() => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }, []);

  // Context value to provide to consumers
  const value = {
    user,
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

// Hook to access authentication context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
