import { useState, useEffect, useRef } from 'react';
import ContextMenu from '../common/ContextMenu';
import LabelDialog from '../label/LabelDialog';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';

/**
 * LabelContextMenu component.
 * Renders a right-click menu to add or remove labels from a specific mail.
 * Uses checkboxes to reflect current label state and updates via toggleLabel.
 * Positioned absolutely at the clicked location.
 */
const LabelContextMenu = ({ mailId, position, onClose }) => {
  const { labels, refreshLabels, labelService } = useLabels();
  const { mails, toggleLabel } = useMail();

  const [showDialog, setShowDialog] = useState(false);
  const [dialogError, setDialogError] = useState('');
  const [adjustedPos, setAdjustedPos] = useState(position);

  const menuRef = useRef(null);
  const mail = mails.find(m => m.id === mailId);


  useEffect(() => {
    const frame = requestAnimationFrame(() => {
      if (!menuRef.current) return;

      const menuRect = menuRef.current.getBoundingClientRect();
      const screenWidth = window.innerWidth;
      const screenHeight = window.innerHeight;

      let newX = position.x;
      let newY = position.y;

      if (position.x + menuRect.width > screenWidth) {
        newX = Math.max(0, screenWidth - menuRect.width - 8);
      }

      const fitsBelow = position.y + menuRect.height <= screenHeight;
      const fitsAbove = position.y >= menuRect.height;

      if (!fitsBelow && fitsAbove) {
        newY = position.y - menuRect.height;
      }

      if (!fitsBelow && !fitsAbove) {
        newY = screenHeight - menuRect.height;
      }

      setAdjustedPos({ x: newX, y: newY });
    });

    return () => cancelAnimationFrame(frame);
  }, [position]);



  const selectedLabelIds = Array.isArray(mail?.labels)
    ? mail.labels.map(l => l.id)
    : [];

  const handleToggle = async (labelId) => {
    await toggleLabel(mailId, labelId);
  };

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

  const items = [
    { label: 'Label as:', type: 'title' },
    ...labels.map((label) => ({
      type: 'checkbox',
      label: label.name,
      checked: selectedLabelIds.includes(label.id),
      onClick: () => handleToggle(label.id),
    })),
    { type: 'divider' },
    {
      label: 'Create new',
      onClick: () => {
        onClose();
        setShowDialog(true);
      },
    },
  ];

  return (
    <>
      <div
        style={{
          position: 'absolute',
          top: `${adjustedPos.y}px`,
          left: `${adjustedPos.x}px`,
          zIndex: 9999
        }}
      >
        <ContextMenu ref={menuRef} items={items} onClose={onClose} />
      </div>


      {showDialog && (
        <LabelDialog
          mode="new"
          existingLabels={labels.map(l => l.name)}
          onCancel={() => {
            setDialogError('');
            setShowDialog(false);
          }}
          onCreate={handleCreateLabel}
          externalError={dialogError}
          clearExternalError={() => setDialogError('')}
        />
      )}
    </>
  );
};

export default LabelContextMenu;
