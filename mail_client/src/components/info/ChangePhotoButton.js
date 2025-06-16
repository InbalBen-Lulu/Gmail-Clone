import { useState } from 'react';
import Icon from '../../assets/icons/Icon';
import ProfileImage from '../common/profile_image/ProfileImage';
import ProfilePictureDialog from './ProfilePictureDialog';
import Overlay from '../overlay/Overlay';
import './ChangePhotoButton.css';

/**
 * ChangePhotoButton component.
 * Displays a circular profile image (70px) with a camera icon.
 * When clicked, opens a ProfilePictureDialog to select a new image.
 *
 * Props:
 * - src: current image URL
 * - alt: optional alt text
 * - onFileSelect: callback when a new image file is selected
 */
const ChangePhotoButton = ({ src, alt = "profile", onFileSelect }) => {
  const [isDialogOpen, setDialogOpen] = useState(false);

  const handleClick = () => {
    setDialogOpen(true); // Open the popup dialog
  };

  const handleCloseDialog = () => {
    setDialogOpen(false); // Close the dialog
  };

  const handleFileChange = (file) => {
    onFileSelect?.(file);
    setDialogOpen(false);
  };

  return (
    <>
      {/* Button with profile image and camera icon */}
      <button
        className="change-photo-button"
        onClick={handleClick}
        aria-label="Change profile photo"
      >
        <ProfileImage src={src} alt={alt} size="100%" />
        <div className="image-overlay" />
        <div className="camera-icon-wrapper">
          <Icon name="photo_camera" className="camera-icon" />
        </div>
      </button>

      {/* Overlay and dialog shown only when open */}
      {isDialogOpen && (
        <>
          <Overlay />
          <ProfilePictureDialog
            imageSrc={src}
            onClose={handleCloseDialog}
            onFileSelect={handleFileChange}
          />
        </>
      )}
    </>
  );
};

export default ChangePhotoButton;
