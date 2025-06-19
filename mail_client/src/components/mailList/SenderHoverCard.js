import './SenderHoverCard.css';
import TextButton from '../common/button/TextButton';

/**
 * SenderHoverCard displays sender details on hover.
 */
const SenderHoverCard = ({ name, email, imageUrl, onSendMail }) => {
  return (
    <div className="sender-hover-card">
      <div className="sender-hover-top">
        <img className="sender-hover-avatar" src={imageUrl} />
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
