import { useState } from 'react';
import MailItem from './MailItem';
import LabelContextMenu from './LabelContextMenu';
import { useMail } from '../../contexts/MailContext';

/**
 * MailList renders a list of MailItem components.
 * Supports right-click to open a label context menu (unless the mail is spam).
 * Props:
 * - onClick: function to call when a mail is clicked (usually to view details).
 */
const MailList = ({ onClick }) => {
    const { mails } = useMail();
    const [menuPosition, setMenuPosition] = useState(null);

    const [contextMenuMailId, setContextMenuMailId] = useState(null);

    const handleRightClick = (e, mail) => {
        e.preventDefault();
        if (mail?.isSpam) return;

        setContextMenuMailId(mail.id);
        setMenuPosition({ x: e.pageX, y: e.pageY });
    };

    const showContextMenu = !!(
        menuPosition &&
        contextMenuMailId !== null &&
        !mails.find(m => m.id === contextMenuMailId)?.isSpam
    );

    return (
        <div>
            {mails.map((mail) => (
                <div key={mail.id} onContextMenu={(e) => handleRightClick(e, mail)}>
                    <MailItem mail={mail} onClick={onClick} />
                </div>
            ))}

            {showContextMenu && (
                <LabelContextMenu
                    mailId={contextMenuMailId}
                    position={menuPosition}
                    onClose={() => {
                        setMenuPosition(null);
                        setContextMenuMailId(null);
                    }}
                />
            )}

        </div>
    );
};

export default MailList;
