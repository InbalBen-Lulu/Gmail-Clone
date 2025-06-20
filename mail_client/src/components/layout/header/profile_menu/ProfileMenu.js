import ChangePhotoButton from '../../../common/profile_image/ChangePhotoButton';
import TextButton from '../../../common/button/TextButton';
import Icon from '../../../../assets/icons/Icon';
import { MainIconButton } from '../../../common/button/IconButtons';
import './ProfileMenu.css';
// import { useAuth } from '../../contexts/AuthContext';
// import { useNavigate } from 'react-router-dom';

/**
 * ProfileMenu component.
 * Displays the user's profile popup with name, email, profile image, and manage button.
 */
const ProfileMenu = ({ onClose }) => {
//   const { user } = useAuth();
//   const navigate = useNavigate();

  const user = {
    name: 'ענבל',
    email: 'inbal014789@gmail.com',
    profileImage: '/pics/demo_profile.png',
  };

  const handleManageAccount = () => {
    // navigate('/personal-info');
    console.log('Manage your Google Account clicked');
  };

  const handleSignOut = () => {
    console.log('Logout clicked');
  };

  return (
    <div className="profile-menu">
      {/* Top section: email + image + hi + name */}
      <MainIconButton
        icon={<Icon name="close" />}
        ariaLabel="Close profile menu"
        onClick={onClose}
        className="close-button"
      />
      <p className="email">{user.email}</p>

      <div className="profile-image-wrapper">
         {/* <ChangePhotoButton /> */}
        <ChangePhotoButton image={user.profileImage} size="95px" />
      </div>

      <div className="greeting-text">
        <span className="hi-label">Hi,&nbsp;</span>
        <span className="name-label">{user.name}!</span>
      </div>

      {/* Manage account button */}
      <TextButton onClick={handleManageAccount} variant="ghost">
        View personal info
      </TextButton>

      <button className="signout-button" onClick={handleSignOut}>
        <Icon name="logout" />
        <span className="signout-text">Sign out</span>
      </button>

    </div>
  );
};

export default ProfileMenu;
