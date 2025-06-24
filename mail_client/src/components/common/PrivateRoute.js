import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

/**
 * PrivateRoute ensures that only authenticated users can access the children.
 * If the user is not authenticated, it redirects to the signin page.
 */
const PrivateRoute = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return null;

  return isAuthenticated ? children : <Navigate to="/signin" replace />;
};

export default PrivateRoute;
