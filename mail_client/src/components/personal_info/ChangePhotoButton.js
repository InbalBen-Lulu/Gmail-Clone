import { useState, useEffect } from 'react';
import Icon from '../../assets/icons/Icon';
import ProfileImage from '../common/profile_image/ProfileImage';
import ProfilePictureDialog from './ProfilePictureDialog';
import Overlay from '../overlay/Overlay';
import { useUserService } from '../../services/useUserService';
import { useAuth } from '../../contexts/AuthContext';
import './ChangePhotoButton.css';

/**
 * ChangePhotoButton component.
 * Displays a circular profile image with a camera icon overlay.
 * On click, opens a dialog to update or remove the profile picture.
 *
 * Props:
 * - alt (optional): alternative text for the profile image
 */
const ChangePhotoButton = ({ alt = "profile" }) => {
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const { uploadProfileImage, removeProfileImage } = useUserService();
  const { user, setUser } = useAuth();

  useEffect(() => {
    const shouldOpen = localStorage.getItem('openDialogAfterReload');
    if (shouldOpen === 'true') {
      setDialogOpen(true);
      localStorage.removeItem('openDialogAfterReload');
    }
  }, []);

  const handleClick = () => {
    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
  };

  const handleFileChange = async (base64Image) => {
    setIsUpdating(true);
    try {
      const newImageUrl = await uploadProfileImage(user.userId, base64Image);
      const updatedUser = { ...user, profileImage: newImageUrl, hasCustomImage: true };
      setUser(updatedUser);
      localStorage.setItem('user', JSON.stringify(updatedUser));
      localStorage.setItem('openDialogAfterReload', 'true');
      window.location.reload();
    } catch (error) {
      alert(error.message);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleRemoveImage = async () => {
    setIsUpdating(true);
    try {
      const newImageUrl = await removeProfileImage(user.userId);
      const updatedUser = { ...user, profileImage: newImageUrl, hasCustomImage: false };
      setUser(updatedUser);
      localStorage.setItem('user', JSON.stringify(updatedUser));
    } catch (error) {
      alert(error.message);
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <>
      <button className="change-photo-button" onClick={handleClick} aria-label="Change profile photo">
        <ProfileImage src={user.profileImage} alt={alt} size="100%" />
        <div className="image-overlay" />
        <div className="camera-icon-wrapper">
          <Icon name="photo_camera" className="camera-icon" />
        </div>
      </button>

      {isDialogOpen && (
        <>
          <Overlay onClick={handleCloseDialog} />
          <ProfilePictureDialog
            imageSrc={user.profileImage}
            hasCustomImage={user.hasCustomImage}
            onClose={handleCloseDialog}
            onFileSelect={handleFileChange}
            onRemove={handleRemoveImage}
            isUpdating={isUpdating}
          />
        </>
      )}
    </>
  );
};

export default ChangePhotoButton;
