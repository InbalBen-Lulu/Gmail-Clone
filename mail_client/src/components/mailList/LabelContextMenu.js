import ContextMenu from '../common/ContextMenu';
import addIcon from '../../assets/icons/add.svg';

/**
 * LabelContextMenu displays a list of labels with checkboxes.
 * @param {Object} props
 * @param {{ id: string, name: string }[]} props.labels - List of available labels.
 * @param {string[]} props.selectedLabelIds - Array of currently selected label ids.
 * @param {function(string):void} props.onToggleLabel - Callback when a label is toggled.
 * @param {{ x: number, y: number }} props.position - Position to render the menu.
 * @param {function} props.onClose - Called when menu is closed (e.g. click outside).
 */
const LabelContextMenu = ({ labels = [], selectedLabelIds = [], onToggleLabel, position, onClose }) => {
  if (!position) return null;

  const items = [
    { label: 'Label as:', type: 'title' },
    ...labels.map((label) => ({
      type: 'checkbox',
      label: label.name,
      checked: selectedLabelIds.includes(label.id),
      onClick: () => onToggleLabel(label.id),
    })),
    { type: 'divider' },
    {
      label: 'Create new',
      onClick: () => {
        alert('Create label clicked');
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
