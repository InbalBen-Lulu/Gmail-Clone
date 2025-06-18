import './ProfileImage.css';

/**
 * ProfileImage component.
 * Displays an image in a circular frame of a given size.
 * Can be reused in different places with different dimensions and styles.
 *
 * Props:
 * - src: the image URL
 * - alt: alternative text (default: "profile")
 * - size: width/height of the circle (e.g., "150px", "6rem")
 * - className: optional additional CSS classes for custom styling
 */
const ProfileImage = ({ src, alt = "profile", size = "150px", className = "" }) => {
  const apiBase = process.env.REACT_APP_API_BASE_URL;
  const fullSrc = src?.startsWith('/profilePics') ? `${apiBase}${src}` : src;

  return (
    <div
      className={`profile-image ${className}`.trim()}
      style={{ width: size, height: size }}
    >
      <img src={fullSrc} alt={alt} />
    </div>
  );
};

export default ProfileImage;
