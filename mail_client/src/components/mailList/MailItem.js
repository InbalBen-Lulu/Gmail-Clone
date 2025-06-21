import './MailItem.css';
import { TrashButton, StarButton } from '../common/button/IconButtons';
import { formatMailDate } from '../../utils/dateUtils';
import LabelChip from './LabelChip';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';

/**
 * MailItem displays a single row in the mail list.
 * Shows sender/recipient, labels, subject, body preview, timestamp, star & delete icons.
 * Clicking opens full mail view; star and trash actions are handled independently.
 */
const MailItem = ({ mail: propMail, onClick }) => {
    const { mails, deleteMail, toggleStar } = useMail();
    const { labels: allLabels } = useLabels();

    const mail = mails.find(m => m.id === propMail.id) || propMail;

    const {
        id,
        subject,
        body,
        sentAt,
        isStar,
        labels = [],
        from,
        to = [],
        isDraft = false,
        isRead = false,
        isSpam = false,
        type
    } = mail;

    const decodedBody = decodeURIComponent(body || '');

    const labelIds = mail.labels.map(l => l.id);
    const mailLabels = allLabels?.filter(label => labelIds.includes(label.id));

    const senderOrRecipient = isDraft
        ? 'Draft'
        : type === 'sent'
            ? `To: ${to.join(', ')}`
            : from?.name || from?.userId || 'Unknown';

    return (
        <div className={`mail-item ${isRead ? 'read' : ''}`} onClick={() => onClick?.(id)}>
            {/* LEFT: Star + "Draft" , Labels*/}
            <div className="mail-left">
                {!isSpam && (
                    <StarButton
                        isStarred={isStar}
                        onClick={(e) => {
                            e.stopPropagation();
                            toggleStar(id);
                        }}
                    />
                )}
                <span className={`mail-sender ${isDraft ? 'draft-label' : ''}`}>
                    {senderOrRecipient}
                </span>

                <div className="labels-container">
                    {mailLabels.map(label => (
                        <LabelChip key={label.id} name={label.name} color={label.color} />
                    ))}
                </div>
            </div>

            {/* CENTER: Subject, Body */}
            <div className="mail-content">
                <div className="mail-subject">
                    <div className="subject-container">
                        <span className="subject">{subject || '(no subject)'}</span>
                    </div>
                </div>

                {decodedBody && (
                    <div className="body-container">
                        <span className="separator">–</span>
                        <span className="body-preview">{decodedBody}</span>
                    </div>
                )}
            </div>

            {/* RIGHT: Time + Trash */}
            <div className="mail-right">
                <span className="mail-time">{formatMailDate(sentAt)}</span>
                <div className="trash-wrapper">
                    <TrashButton
                        onClick={(e) => {
                            e.stopPropagation();
                            deleteMail(id);
                        }}
                    />
                </div>
            </div>
        </div>
    );
};

export default MailItem;
