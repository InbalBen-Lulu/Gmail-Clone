import { useRef, useState } from 'react';
import ProfileImage from '../../common/profile_image/ProfileImage';
import TextButton from '../../common/button/TextButton';
import { resizeAndConvertToBase64 } from '../../../utils/imageUtils';
import { ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE_BYTES } from '../../personal_info/constants';
import { useUserService } from '../../../services/useUserService';
import { useAuth } from '../../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useRef, useState } from 'react';
import ProfileImage from '../../common/profile_image/ProfileImage';
import TextButton from '../../common/button/TextButton';
import { resizeAndConvertToBase64 } from '../../../utils/imageUtils';
import { ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE_BYTES } from '../../personal_info/constants';
import { useUserService } from '../../../services/useUserService';
import { useAuth } from '../../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

/**
 * Step5Photo component – last signup step for uploading a profile image.
 *
 * Props:
 * - (none) – the user is already created and logged in, so image will be sent via token
 */
const Step5Photo = () => {
  const fileInputRef = useRef(null);
  const [imageData, setImageData] = useState(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const [error, setError] = useState(null);

  const { uploadProfileImage } = useUserService();
  const { user, setUser } = useAuth();
  const navigate = useNavigate();

  const imagePreview = imageData || user?.profileImage;

  // Triggers the hidden file input
  const handleUploadClick = () => {
    if (!isUpdating) fileInputRef.current?.click();
  };

  // Handles image upload
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Clear any previous error before validating the new file
    setError('');

    if (!file.type.startsWith('image/') || !ALLOWED_IMAGE_TYPES.includes(file.type)) {
      setError('Please upload a JPG or PNG image.');
      return;
    }

    if (file.size > MAX_IMAGE_SIZE_BYTES) {
      setError('Image too large. Max 5MB allowed.');
      return;
    }

    setIsUpdating(true);
    try {
      const base64 = await resizeAndConvertToBase64(file);
      setImageData(base64);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleRemove = () => {
    setImageData(null);
  };

  // Submits image if selected, then navigates to inbox
  const handleContinue = async () => {
    if (imageData) {
      setIsUpdating(true);
      try {
        const newImageUrl = await uploadProfileImage(user.userId, imageData);
        const updatedUser = { ...user, profileImage: newImageUrl, hasCustomImage: true };
        setUser(updatedUser);
        localStorage.setItem('user', JSON.stringify(updatedUser));
      } catch (err) {
        setError(err.message);
        return;
      } finally {
        setIsUpdating(false);
      }
    }
    navigate('/mails/inbox');
  };

  return (
    <div className="form-group">
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <ProfileImage src={imagePreview} size="200px" style={{ marginTop: '-1rem' }} />

        <input
          type="file"
          ref={fileInputRef}
          accept="image/jpeg, image/png, image/webp"
          style={{ display: 'none' }}
          onChange={handleFileChange}
        />

        <div className="button-row" style={{ marginTop: '2rem', justifyContent: 'center' }}>
          {!imageData ? (
            <>
              <TextButton variant="primary" onClick={handleUploadClick} disabled={isUpdating}>
                Add photo
              </TextButton>
              <TextButton variant="ghost" onClick={handleContinue} disabled={isUpdating}>
                Skip
              </TextButton>
            </>
          ) : (
            <>
              <TextButton variant="primary" onClick={handleContinue} disabled={isUpdating}>
                Continue
              </TextButton>
              <TextButton variant="ghost" onClick={handleRemove} disabled={isUpdating}>
                Remove
              </TextButton>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Step5Photo;
