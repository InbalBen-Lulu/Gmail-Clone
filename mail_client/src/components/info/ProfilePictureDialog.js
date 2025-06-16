// import { useRef } from 'react';
// import Icon from '../../assets/icons/Icon';
// import { MainIconButton } from '../common/button/IconButtons';
// import { ProfileActionButton } from '../common/button/IconTextButton';
// import ProfileImage from '../common/profile_image/ProfileImage';
// import './ProfilePictureDialog.css';

// /**
//  * ProfilePictureDialog component.
//  * Displays a modal for viewing and updating the user's profile picture.
//  * Includes close button, title, preview image, and upload functionality.
//  * Props:
//  * - onClose: closes the dialog
//  * - onFileSelect: handles selected image file
//  * - imageSrc: optional image URL to display
//  */
// const ProfilePictureDialog = ({ onClose, onFileSelect, imageSrc }) => {
//   const fileInputRef = useRef(null);

//   // Trigger file input click when upload button is clicked
//   const handleUploadClick = () => {
//     fileInputRef.current?.click();
//   };

//   // Handle file selection
//   const handleFileChange = (e) => {
//     const file = e.target.files[0];
//     if (file && onFileSelect) {
//       onFileSelect(file);
//     }
//   };

//   return (
//       <div className="dialog-box">
//         {/* Top header: Close button and Account label */}
//         <div className="dialog-header">
//             <MainIconButton
//               icon={<Icon name="close" />}
//               ariaLabel="Close"
//               onClick={onClose}
//               className="dialog-close-btn"
//             />
//             <span className="dialog-label">
//             <img
//                 src="/pics/gmail_logo_icon.ico"
//                 alt="Gmail Logo"
//                 className="gmail-icon"
//             />
//             <span>Gmail Account</span> 
//             </span>
//         </div>

//         {/* Dialog title */}
//         <h2 className="dialog-title">Profile picture</h2>
        
//         {/* Subtext below the title */}
//         <p className="dialog-subtext">
//           A picture helps people recognize you and lets you know when you’re signed in to your account
//         </p>

//         {/* Circular profile image using reusable component */}
//         <ProfileImage
//           src={imageSrc || "/default-profile.png"}
//           size="285px"
//           className="dialog-profile-pic"
//         />

//         {/* Upload profile picture button */}
//         <ProfileActionButton iconName="add_a_photo" text="Add profile picture" onClick={handleUploadClick } />

//         {/* Hidden file input */}
//         <input
//           type="file"
//           accept="image/*"
//           ref={fileInputRef}
//           onChange={handleFileChange}
//           style={{ display: 'none' }}
//         />
//       </div>
//   );
// };

// export default ProfilePictureDialog;


import { useRef } from 'react';
import Icon from '../../assets/icons/Icon';
import { MainIconButton } from '../common/button/IconButtons';
import { ProfileActionButton } from '../common/button/IconTextButton';
import ProfileImage from '../common/profile_image/ProfileImage';
import './ProfilePictureDialog.css';

/**
 * ProfilePictureDialog component.
 * Displays a modal dialog to view and manage a user's profile picture.
 *
 * Props:
 * - onClose: function – called when dialog should close
 * - onFileSelect: function – receives selected image file
 * - imageSrc: string – optional image URL to display (if not provided, default image is shown)
 */
const ProfilePictureDialog = ({ onClose, onFileSelect, imageSrc }) => {
  const fileInputRef = useRef(null);

  // Determine whether the image is the default one
  const isDefaultImage = !imageSrc || imageSrc.includes("default-profile.png");

  // Trigger file input click when upload/change button is clicked
  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  // Handle file selection
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && onFileSelect) {
      onFileSelect(file);
    }
  };

  return (
    <div className="dialog-box">
      {/* Top header: Close button and Account label */}
      <div className="dialog-header">
        <MainIconButton
          icon={<Icon name="close" />}
          ariaLabel="Close"
          onClick={onClose}
          className="dialog-close-btn"
        />
        <span className="dialog-label">
          <img
            src="/pics/gmail_logo_icon.ico"
            alt="Gmail Logo"
            className="gmail-icon"
          />
          <span>Gmail Account</span>
        </span>
      </div>

      {/* Dialog title */}
      <h2 className="dialog-title">Profile picture</h2>

      {/* Subtext below the title */}
      <p className="dialog-subtext">
        A picture helps people recognize you and lets you know when you’re signed in to your account
      </p>

      {/* Circular profile image using reusable component */}
      <ProfileImage
        src={imageSrc || "/default-profile.png"}
        size="285px"
        className="dialog-profile-pic"
      />

      {/* Action buttons below the image */}
      <div className="profile-actions">
        {isDefaultImage ? (
          <ProfileActionButton
            iconName="add_a_photo"
            text="Add profile picture"
            onClick={handleUploadClick}
            width="100%"
          />
        ) : (
          <>
            <ProfileActionButton
              iconName="edit"
              text="Change"
              onClick={handleUploadClick}
              width="170px"
            />
            <ProfileActionButton
              iconName="delete"
              text="Remove"
              onClick={() => console.log("Remove clicked")}
              width="170px"
            />
          </>
        )}
      </div>

      {/* Hidden file input */}
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


