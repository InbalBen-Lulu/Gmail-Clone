import { useState, useEffect } from 'react';
import Icon from '../../../assets/icons/Icon';
import ProfileImage from './ProfileImage';
import ProfilePictureDialog from './ProfilePictureDialog';
import Overlay from '../../overlay/Overlay';
import { useUserService } from '../../../services/useUserService';
import { useAuth } from '../../../contexts/AuthContext';
import './ChangePhotoButton.css';

/**
 * ChangePhotoButton component.
 * Displays a circular profile image with a camera icon overlay.
 * On click, opens a dialog to update or remove the profile picture.
 *
 * Props:
 * - alt (optional): alternative text for the profile image
 * - size (optional): image size in pixels (default: "70")
 */
const ChangePhotoButton = ({ alt = "profile", size = "70" }) => {
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const { uploadProfileImage, removeProfileImage } = useUserService();
  const { user, setUser } = useAuth();

  // On first render, check if the dialog should auto-open after reload
  useEffect(() => {
    const shouldOpen = localStorage.getItem('openDialogAfterReload');
    if (shouldOpen === 'true') {
      setDialogOpen(true);
      localStorage.removeItem('openDialogAfterReload');
    }
  }, []);

  /**
   * Opens the profile picture dialog.
   */
  const handleClick = () => {
    setDialogOpen(true);
  };

  /**
   * Closes the profile picture dialog.
   */
  const handleCloseDialog = () => {
    setDialogOpen(false);
  };

  /**
   * Handles the upload of a new profile image.
   * Encodes the image, uploads it to the server, updates user state,
   * and forces a reload to reflect the change.
   *
   * @param {string} base64Image - base64 encoded image string
   */
  const handleFileChange = async (base64Image) => {
    setIsUpdating(true);
    try {
      const newImageUrl = await uploadProfileImage(user.userId, base64Image);
      const updatedUser = { ...user, profileImage: newImageUrl, hasCustomImage: true };
      setUser(updatedUser);
      localStorage.setItem('user', JSON.stringify(updatedUser));
      localStorage.setItem('openDialogAfterReload', 'true');
      window.location.reload(); // reload to reflect image change immediately
    } catch (error) {
      alert(error.message);
    } finally {
      setIsUpdating(false);
    }
  };

  /**
   * Handles removal of the user's custom profile image.
   * Sends request to server and updates user state accordingly.
   */
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
      {/* Profile image button with overlay and camera icon */}
      <button
        className="change-photo-button"
        onClick={handleClick}
        aria-label="Change profile photo"
      >
        <ProfileImage src={user.profileImage} alt={alt} size={size} />
        <div className="image-overlay" />
        <div className="camera-icon-wrapper">
          <Icon name="photo_camera" className="camera-icon" />
        </div>
      </button>

      {/* Conditional rendering of the dialog */}
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
