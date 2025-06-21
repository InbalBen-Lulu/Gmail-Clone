import './MailDetails.css';
import { TrashButton, StarButton, SmallIconButton } from '../common/button/IconButtons';
import Icon from "../../assets/icons/Icon";
import LabelChip from './LabelChip';
import { formatMailDate } from '../../utils/dateUtils';
import { useState, useRef, useEffect } from 'react';
import SenderHoverCard from './SenderHoverCard';
import { useCompose } from '../../contexts/ComposeContext';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';
import { EMAIL_DOMAIN } from '../personal_info/constants';
import ProfileImage from '../common/profile_image/ProfileImage';

/**
 * MailDetails displays the full content of a selected mail.
 * Includes subject, labels, sender info with hover card, actions, and body.
 */
const MailDetails = ({ mail, onBack }) => {
    const [showCard, setShowCard] = useState(false);
    const hoverTimeoutRef = useRef(null);
    const { openCompose } = useCompose();
    const { toggleStar, deleteMail, setSpamStatus } = useMail();
    const { labels: allLabels } = useLabels();
    const [localIsSpam, setLocalIsSpam] = useState(false);

    const {
        id,
        subject,
        body,
        sentAt,
        isStar,
        labels = [],
        type,
        from,
        to = [],
        isSpam
    } = mail;

    useEffect(() => {
        setLocalIsSpam(isSpam);
    }, [isSpam]);

    const decodedBody = decodeURIComponent(body || '');
    const labelIds = labels.map(l => l.id);
    const mailLabels = allLabels?.filter(label => labelIds.includes(label.id)) || [];

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

    const handleSpamToggle = async () => {
        await setSpamStatus(id, !isSpam);
        setLocalIsSpam(!isSpam);
    };

    return (
        <div className="mail-details">
            {/* Topbar */}
            <div className="mail-details-topbar">
                <SmallIconButton icon={<Icon name="arrow_back2" />} ariaLabel="Back" onClick={onBack} />

                <div className="mail-details-actions">
                    {type !== 'sent' && (
                        <SmallIconButton
                            icon={<Icon name={isSpam ? 'not_spam' : 'report'} />}
                            title={isSpam ? 'Remove from Spam' : 'Report as Spam'}
                            ariaLabel={isSpam ? 'Not Spam' : 'Report Spam'}
                            onClick={handleSpamToggle}
                        />
                    )}

                    <StarButton isStarred={isStar} onClick={() => toggleStar(id)} />
                    <TrashButton onClick={() => deleteMail(id)} />
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
                    <ProfileImage
                        src={from?.profileImage || '/assets/default-avatar.png'}
                        alt={from?.name || 'Sender'}
                        size="40px"
                    />

                    {showCard && (
                        <div
                            className="hover-card-wrapper"
                            onMouseEnter={handleMouseEnter}
                            onMouseLeave={handleMouseLeave}
                        >
                            <SenderHoverCard
                                name={from?.name || from?.userId || 'Unknown'}
                                email={`${from?.userId}@${EMAIL_DOMAIN}`}
                                imageUrl={from?.profileImage || '/assets/default-avatar.png'}
                                onSendMail={() => openCompose(`${from?.userId}@${EMAIL_DOMAIN}`)}
                            />
                        </div>
                    )}
                </div>

                <div className="mail-details-sender-info">
                    <div className="sender-name-row">
                        <strong className="sender-name">{from?.name || from?.userId}</strong>
                        <span className="sender-email">&lt;{from?.userId}@{EMAIL_DOMAIN}&gt;</span>
                    </div>
                    {to.length > 0 && (
                        <div className="mail-details-to">to {to.join(', ')}</div>
                    )}
                </div>

                <div className="mail-details-date">{formatMailDate(sentAt)}</div>
            </div>

            {/* Body */}
            <div className="mail-details-body">{decodedBody}</div>
        </div>
    );
};

export default MailDetails;
