import { useAuth } from '../contexts/AuthContext';
import UserSection from '../components/personal_info/UserSection';

/**
 * PersonalInfoPage
 * Displays the personal information of the currently logged-in user.
 * Handles loading state and access control.
 */
const PersonalInfoPage = () => {
  const { user, isLoading, isAuthenticated } = useAuth();

  // Show a loading message (can be replaced later with a spinner)
  if (isLoading) {
    return <p>Loading...</p>;
  }

  // If not logged in – show message (or redirect later)
  if (!isAuthenticated) {
    return <p>You must be logged in to view this page.</p>; 
  }

  // Main content – user's personal info section
  return (
    <div className="personal-info-page">
      <UserSection user={user} />
    </div>
  );
};

export default PersonalInfoPage;
