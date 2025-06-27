import { useState, useEffect } from 'react';
import MailItem from './MailItem';
import LabelMailContextMenu from './LabelMailContextMenu';
import LabelDialog from '../label/LabelDialog';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';
import { useLabelService } from '../../services/useLabelService';

/**
 * MailList renders a list of MailItem components.
 * Supports right-click to open a label context menu (unless the mail is spam).
 * Props:
 * - onClick: function to call when a mail is clicked (usually to view details).
*/
const MailList = ({ onClick }) => {
  const [menuPosition, setMenuPosition] = useState(null);
  const [contextMenuMailId, setContextMenuMailId] = useState(null);
  const [showDialog, setShowDialog] = useState(false);
  const [dialogError, setDialogError] = useState('');

  const { mails } = useMail();
  const { labels, refreshLabels } = useLabels();
  const labelService = useLabelService();

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

  /**
   * Handles creation of a new label via dialog
   * @param {string} name
   */
  const handleCreateLabel = async (name) => {
    try {
      await labelService.createLabel(name);
      await refreshLabels();
      setDialogError('');
      setShowDialog(false);
    } catch (err) {
      if (err.message.includes('already exists')) {
        setDialogError('The label name you have chosen already exists. Please try another name');
      } else {
        setDialogError('Failed to create label.');
      }
    }
  };

  return (
    <div>
      {mails.map(mail => (
        <div key={mail.id} onContextMenu={(e) => handleRightClick(e, mail)}>
          <MailItem mail={mail} onClick={onClick} />
        </div>
      ))}

      {showContextMenu && (
        <LabelMailContextMenu
          mailId={contextMenuMailId}
          position={menuPosition}
          onClose={() => {
            setMenuPosition(null);
            setContextMenuMailId(null);
          }}
          onRequestCreateNew={() => {
            setMenuPosition(null);
            setContextMenuMailId(null);
            setShowDialog(true);
          }}
        />
      )}

      {showDialog && (
        <LabelDialog
          mode="new"
          existingLabels={labels.map(l => l.name)}
          onCancel={() => {
            setShowDialog(false);
            setDialogError('');
          }}
          onCreate={handleCreateLabel}
          externalError={dialogError}
          clearExternalError={() => setDialogError('')}
        />
      )}
    </div>
  );
};

export default MailList;
