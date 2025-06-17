import './UserSection.css';
import ChangePhotoButton from './ChangePhotoButton';

/**
 * UserSection component
 * Displays personal and contact info of a user inside styled boxes.
 *
 * Props:
 * - user: { name, email, birthDate, gender, phone, profileImage }
 */
const UserSection = ({ user }) => {
  const {
    name,
    email,
    birthDate,
    gender,
    phone,
    profileImage,
  } = user;

  const handleNewImage = (file) => {
    console.log('New image selected:', file);
  };

  return (
    <section className="user-section">
      {/* Page header section */}
      <div className="page-header">
        <h1 className="main-page-title">Personal info</h1>
        <p className="main-page-subtitle">
          Info about you and your preferences across Google services
        </p>
      </div>

      {/* Box: Basic Info */}
      <div className="user-info-box">
        <div className="box-header">
          <h2>Basic info</h2>
          <p className="box-subtext">
            Some info may be visible to other people using our services.
          </p>
        </div>

        <div className="user-info-details">
            {/* Row: Profile picture (with ChangePhotoButton) */}
          <div className="user-info-row full">
            <span className="user-info-label">Profile picture</span>
            <span className="user-info-value">
              Add a profile picture to personalize your account
            </span>
            <div className="profile-picture-cell">
              <ChangePhotoButton
                src={profileImage}
                onFileSelect={handleNewImage}
              />
            </div>
          </div>

          {/* Info rows */}
          <div className="user-info-row">
            <span className="user-info-label">Name</span>
            <span className="user-info-value">{name}</span>
          </div>
          <div className="user-info-row">
            <span className="user-info-label">Birthday</span>
            <span className="user-info-value">{birthDate}</span>
          </div>
          <div className="user-info-row">
            <span className="user-info-label">Gender</span>
            <span className="user-info-value">{gender}</span>
          </div>
        </div>
      </div>

      {/* Box: Contact Info */}
      <div className="user-info-box">
        <div className="box-header no-subtext">
          <h2>Contact info</h2>
        </div>

        <div className="user-info-details">
          <div className="user-info-row">
            <span className="user-info-label">Email</span>
            <span className="user-info-value">{email}</span>
          </div>
          <div className="user-info-row">
            <span className="user-info-label">Phone</span>
            <span className="user-info-value">{phone}</span>
          </div>
        </div>
      </div>
    </section>
  );
};

export default UserSection;
