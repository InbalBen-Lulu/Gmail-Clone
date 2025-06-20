import { useRef } from 'react';
import Icon from '../../../assets/icons/Icon';
import { MainIconButton } from '../button/IconButtons';
import { ProfileActionButton } from '../button/IconTextButton';
import ProfileImage from './ProfileImage';
import { resizeAndConvertToBase64 } from '../../../utils/imageUtils';
import { ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE_BYTES } from '../../personal_info/constants';
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
 * - hasCustomImage: boolean – whether the image is custom or default
 * - isUpdating: boolean – whether image is being uploaded or removed
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

  /**
   * Triggers the hidden file input (if not currently updating)
   */
  const handleUploadClick = () => {
    if (!isUpdating) {
      fileInputRef.current?.click();
    }
  };

  /**
   * Validates the selected file, resizes and converts it to base64, then passes to parent
   */
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert("Please upload a valid image file.");
      return;
    }

    if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
      alert("Only JPG or PNG files are allowed.");
      return;
    }

    const maxSizeBytes = MAX_IMAGE_SIZE_BYTES;
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
      {/* Dialog header with close button and Gmail branding */}
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

      {/* Title and description */}
      <h2 className="dialog-title">Profile picture</h2>
      <p className="dialog-subtext">
        A picture helps people recognize you and lets you know when you’re signed in to your account
      </p>

      {/* Profile picture with optional loading overlay */}
      <div className="dialog-profile-container">
        <ProfileImage
          src={imageSrc}
          size="295px"
          className="dialog-profile-pic"
        />
        {isUpdating && (
          <div className="loading-overlay">
            <span className="loading-spinner" />
          </div>
        )}
      </div>

      {/* Action buttons – conditional on whether a custom image exists */}
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

      {/* Hidden file input to trigger image selection */}
      <input
        type="file"
        accept={ALLOWED_IMAGE_TYPES.join(',')}
        ref={fileInputRef}
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />
    </div>
  );
};

export default ProfilePictureDialog;
