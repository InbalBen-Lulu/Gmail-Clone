import { useRef } from 'react';
import Icon from '../../assets/icons/Icon';
import { MainIconButton } from '../common/button/IconButtons';
import { ProfileActionButton } from '../common/button/IconTextButton';
import ProfileImage from '../common/profile_image/ProfileImage';
import { resizeAndConvertToBase64 } from '../../utils/imageUtils';
import './ProfilePictureDialog.css';

/**
 * ProfilePictureDialog component.
 * Displays a modal dialog to view and manage a user's profile picture.
 *
 * Props:
 * - onClose: function – called when dialog should close
 * - onFileSelect: function – receives base64-encoded image after processing
 * - onRemove: function – called when user wants to remove the profile picture
 * - imageSrc: string – current image URL
 * - hasCustomImage: boolean – indicates whether the image is custom or default
 * - isUpdating: boolean – whether the dialog is currently processing (show spinner)
 */
const ProfilePictureDialog = ({
  onClose,
  onFileSelect,
  onRemove,
  imageSrc,
  hasCustomImage,
  isUpdating
}) => {
  const fileInputRef = useRef(null);

  const handleUploadClick = () => {
    if (!isUpdating) {
      fileInputRef.current?.click();
    }
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert("Please upload a valid image file.");
      return;
    }

    const maxSizeBytes = 5 * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      alert("Image is too large. Please choose a file under 5MB.");
      return;
    }

    try {
      const base64 = await resizeAndConvertToBase64(file);
      onFileSelect(base64);
    } catch (error) {
      console.error("Error processing image:", error);
      alert("Failed to process image.");
    }
  };

  return (
    <div className="dialog-box">
      <div className="dialog-header">
        <MainIconButton
          icon={<Icon name="close" />}
          ariaLabel="Close"
          onClick={onClose}
          className="dialog-close-btn"
        />
        <span className="dialog-label">
          <img src="/pics/gmail_logo_icon.ico" alt="Gmail Logo" className="gmail-icon" />
          <span>Gmail Account</span>
        </span>
      </div>

      <h2 className="dialog-title">Profile picture</h2>
      <p className="dialog-subtext">
        A picture helps people recognize you and lets you know when you’re signed in to your account
      </p>

      {/* Profile image + spinner wrapper */}
      <div className="dialog-profile-container">
        <ProfileImage
          src={imageSrc}
          size="285px"
          className="dialog-profile-pic"
        />
        {isUpdating && (
          <div className="loading-overlay">
            <span className="loading-spinner" />
          </div>
        )}
      </div>

      <div className="profile-actions">
        {!hasCustomImage ? (
          <ProfileActionButton
            iconName="add_a_photo"
            text="Add profile picture"
            onClick={handleUploadClick}
            width="100%"
            disabled={isUpdating}
          />
        ) : (
          <>
            <ProfileActionButton
              iconName="edit"
              text="Change"
              onClick={handleUploadClick}
              width="170px"
              disabled={isUpdating}
            />
            <ProfileActionButton
              iconName="delete"
              text="Remove"
              onClick={onRemove}
              width="170px"
              disabled={isUpdating}
            />
          </>
        )}
      </div>

      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />
    </div>
  );
};

export default ProfilePictureDialog;
