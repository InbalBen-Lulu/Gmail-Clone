import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../../contexts/AuthContext';
import ChangePhotoButton from '../../../common/profile_image/ChangePhotoButton';
import TextButton from '../../../common/button/TextButton';
import Icon from '../../../../assets/icons/Icon';
import { MainIconButton } from '../../../common/button/IconButtons';
import './ProfileMenu.css';

/**
 * ProfileMenu component.
 * Displays the user's profile popup with name, email, profile image, and manage button.
 */
const ProfileMenu = ({ onClose }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  /**
   * Navigates to the personal info page and closes the profile menu.
   */
  const handleManageAccount = () => {
    navigate('/personal-info');
    onClose(); // Close the popup after navigation
  };

  /**
   * Logs out the user and navigates to the login page.
   */
  const handleSignOut = async () => {
    await logout();       // Clear auth
    navigate('/login');   // Go to login page
  };

  return (
    <div className="profile-menu">
      {/* Close (X) button */}
      <MainIconButton
        icon={<Icon name="close" />}
        ariaLabel="Close profile menu"
        onClick={onClose}
        className="close-button"
      />

      {/* Email and profile picture */}
      <p className="email">{user.email}</p>
      <div className="profile-image-wrapper">
        <ChangePhotoButton image={user.profileImage} size="95px" />
      </div>

      {/* Greeting */}
      <div className="greeting-text">
        <span className="hi-label">Hi,&nbsp;</span>
        <span className="name-label">{user.name}!</span>
      </div>

      {/* View personal info */}
      <TextButton onClick={handleManageAccount} variant="ghost">
        View personal info
      </TextButton>

      {/* Sign out */}
      <button className="signout-button" onClick={handleSignOut}>
        <Icon name="logout" />
        <span className="signout-text">Sign out</span>
      </button>
    </div>
  );
};

export default ProfileMenu;
