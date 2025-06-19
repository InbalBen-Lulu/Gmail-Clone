import './MailDetails.css';
import { TrashButton, StarButton, SmallIconButton } from '../common/button/IconButtons';
import Icon from "../../assets/icons/Icon";
import LabelChip from './LabelChip';
import { formatMailDate } from '../../utils/dateUtils';
import { useState, useRef } from 'react';
import SenderHoverCard from './SenderHoverCard';

const MailDetails = ({ mail, allLabels, onStarToggle, onDelete, onBack, onSpam }) => {
  const [showCard, setShowCard] = useState(false);
  const hoverTimeoutRef = useRef(null);

  if (!mail) return null;

  const {
    id,
    subject,
    body,
    sentAt,
    isStar,
    labelIds = [],
    isDraft,
    isSent,
    from,
    to = [],
  } = mail;

  const decodedBody = decodeURIComponent(body || '');
  const mailLabels = allLabels?.filter(label => labelIds.includes(label.id)) || [];

  const senderOrRecipient = isDraft
    ? 'Draft'
    : isSent
      ? `To: ${to.join(', ')}`
      : from;

  const handleMouseEnter = () => {
    clearTimeout(hoverTimeoutRef.current);
    setShowCard(true);
  };

  const handleMouseLeave = () => {
    hoverTimeoutRef.current = setTimeout(() => setShowCard(false), 200);
  };

  const toggleCard = () => {
    setShowCard(prev => !prev);
  };

  return (
    <div className="mail-details">
      {/* Topbar */}
      <div className="mail-details-topbar">
        <SmallIconButton icon={<Icon name="arrow_back2" />} ariaLabel="Back" onClick={onBack} />
        <div className="mail-details-actions">
          <SmallIconButton icon={<Icon name="report" />} ariaLabel="Spam" onClick={onSpam} />
          <StarButton isStarred={isStar} onClick={() => onStarToggle(id)} />
          <TrashButton onClick={() => onDelete(id)} />
        </div>
      </div>

      {/* Subject + labels */}
      <div className="mail-details-subject-row">
        <h1 className="mail-details-subject">{subject || '(no subject)'}</h1>
        <div className="mail-details-labels">
          {mailLabels.map(label => (
            <LabelChip key={label.id} name={label.name} color={label.color} />
          ))}
        </div>
      </div>

      {/* Sender row with profile + hover card */}
      <div className="mail-details-meta">
        <div
          className="profile-wrapper"
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
          onClick={toggleCard}
        >
          <div className="profile-image-placeholder" />
          {showCard && (
            <div
              className="hover-card-wrapper"
              onMouseEnter={handleMouseEnter}
              onMouseLeave={handleMouseLeave}
            >
              <SenderHoverCard
                name="Moria Pedhazur"
                email={from}
                imageUrl="/assets/default-avatar.png"
                onSendMail={() => alert('Send mail')}
                onMore={() => alert('More')}
              />
            </div>
          )}
        </div>

        <div className="mail-details-from">{senderOrRecipient}</div>
        <div className="mail-details-date">{formatMailDate(sentAt)}</div>
      </div>

      {/* Body */}
      <div className="mail-details-body">{decodedBody}</div>
    </div>
  );
};

export default MailDetails;
