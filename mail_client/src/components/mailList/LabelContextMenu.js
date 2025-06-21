import ContextMenu from '../common/ContextMenu';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';

/**
 * LabelContextMenu component.
 * Renders a right-click menu to add or remove labels from a specific mail.
 * Uses checkboxes to reflect current label state and updates via toggleLabel.
 * Positioned absolutely at the clicked location.
 */
const LabelContextMenu = ({ mailId, position, onClose }) => {
  const { labels } = useLabels();
  const { mails, toggleLabel } = useMail();

  const mail = mails.find(m => m.id === mailId);

  const selectedLabelIds = Array.isArray(mail?.labels)
    ? mail.labels.map(l => l.id)
    : [];

  const handleToggle = async (labelId) => {
    await toggleLabel(mailId, labelId);
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
      onClick: async () => {
        onClose();
      },
    },
  ];

  return (
    <div style={{ position: 'absolute', top: position.y, left: position.x }}>
      <ContextMenu items={items} onClose={onClose} />
    </div>
  );
};

export default LabelContextMenu;
