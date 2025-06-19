import './MailItem.css';
import { TrashButton, StarButton } from '../common/button/IconButtons';
import { formatMailDate } from '../../utils/dateUtils';
import LabelChip from './LabelChip';

const MailItem = ({ mail, onDelete,  onToggleLabel, onStarToggle, allLabels, onClick, onSpam }) => {
    const {
        id,
        subject,
        body,
        sentAt,
        isStar,
        labelIds = [],
        from,
        to = [],
        isDraft = false,
        isSent = false,
        isRead = false
    } = mail;
    const decodedBody = decodeURIComponent(body || '');

    const mailLabels = allLabels?.filter(label => labelIds.includes(label.id)) || [];

    const senderOrRecipient = isDraft
        ? 'Draft'
        : isSent
            ? `To: ${to.join(', ')}`
            : from;

    return (
        <div className={`mail-item ${isRead ? 'read' : ''}`} onClick={() => onClick?.(id)}>
            {/* LEFT: Star + "Draft" , Labels*/}
            <div className="mail-left">
                <StarButton isStarred={isStar} onClick={() => onStarToggle(id)} />

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
                    <TrashButton onClick={() => onDelete(mail.id)} />
                </div>
            </div>
        </div>
    );
};

export default MailItem;
