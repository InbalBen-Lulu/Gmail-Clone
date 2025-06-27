import { useEffect, useRef, useState } from 'react';
import ContextMenu from '../common/ContextMenu';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';

/**
 * LabelMailContextMenu component.
 * Displays a right-click context menu to add or remove labels for a specific mail item.
 * Uses checkboxes to show current label state and allows toggling labels.
 * Automatically repositions the menu if it would overflow the screen.
 * Props:
 * - mailId (number): ID of the mail to label.
 * - position (object): { x, y } coordinates to position the menu.
 * - onClose (function): Callback to close the menu.
 * - onRequestCreateNew (function): Callback to open label creation dialog.
 */
const LabelMailContextMenu = ({ mailId, position, onClose, onRequestCreateNew }) => {
  const { labels } = useLabels();
  const { mails, toggleLabel } = useMail();

  const menuRef = useRef(null);
  const mail = mails.find(m => m.id === mailId);
  const [adjustedPos, setAdjustedPos] = useState(position);

  const selectedLabelIds = Array.isArray(mail?.labels)
    ? mail.labels.map(l => l.id)
    : [];

  /**
   * Toggles a label on the selected mail
   * @param {number} labelId
   */
  const handleToggle = async (labelId) => {
    await toggleLabel(mailId, labelId);
  };

  const items = [
    { label: 'Label as:', type: 'title' },
    ...labels.map(label => ({
      type: 'checkbox',
      label: label.name,
      checked: selectedLabelIds.includes(label.id),
      onClick: () => handleToggle(label.id),
    })),
    { type: 'divider' },
    {
      label: 'Create new',
      onClick: () => {
        onRequestCreateNew();
        setTimeout(() => {
          onClose();
        }, 0);
      },
    }
  ];

  // Recalculate position if the menu might overflow the screen
  useEffect(() => {
    const frame = requestAnimationFrame(() => {
      const menuEl = menuRef.current;
      if (!menuEl) return;

      const rect = menuEl.getBoundingClientRect();
      const screenWidth = window.innerWidth;
      const screenHeight = window.innerHeight;

      let x = position.x;
      let y = position.y;

      if (x + rect.width > screenWidth) {
        x = Math.max(0, screenWidth - rect.width - 8);
      }

      const fitsBelow = y + rect.height <= screenHeight;
      const fitsAbove = y >= rect.height;

      if (!fitsBelow && fitsAbove) {
        y = y - rect.height;
      }

      if (!fitsBelow && !fitsAbove) {
        y = screenHeight - rect.height;
      }

      setAdjustedPos({ x, y });
    });

    return () => cancelAnimationFrame(frame);
  }, [position]);

  return (
    <ContextMenu
      ref={menuRef}
      items={items}
      onClose={onClose}
      style={{
        position: 'absolute',
        top: `${adjustedPos.y}px`,
        left: `${adjustedPos.x}px`,
        zIndex: 9999,
      }}
    />
  );
};

export default LabelMailContextMenu;
