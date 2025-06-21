import './SenderHoverCard.css';
import TextButton from '../common/button/TextButton';
import ProfileImage from '../common/profile_image/ProfileImage';

/**
 * SenderHoverCard displays sender details on hover.
 */
const SenderHoverCard = ({ name, email, imageUrl, onSendMail }) => {
  return (
    <div className="sender-hover-card">
      <div className="sender-hover-top">
        <ProfileImage src={imageUrl} size="40px" className="sender-hover-avatar" />
        <div className="sender-hover-info">
          <div className="sender-hover-name">{name}</div>
          <div className="sender-hover-email">{email}</div>
        </div>
      </div>

      <div className="sender-hover-footer">
        <TextButton onClick={onSendMail}>Send mail</TextButton>
      </div>
    </div>
  );
};

export default SenderHoverCard;
