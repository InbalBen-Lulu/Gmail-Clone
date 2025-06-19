import React, { useState } from 'react';
import MailItem from './MailItem';
import LabelContextMenu from './LabelContextMenu';

/**
 * MailList renders a list of mails, supports right-click to label each one.
 * @param {Object[]} mails - Array of mail objects.
 * @param {Object[]} allLabels - Array of all label objects { id, name, color }.
 * @param {Function} onToggleLabel - Called with (mailId, labelId) when toggling.
 * @param {Function} onDelete - Called with (mailId) on delete.
 * @param {Function} onStarToggle - Called with (mailId) on star toggle.
 */
const MailList = ({ mails, allLabels, onToggleLabel, onDelete, onStarToggle, onClick, onSpam }) => {
  const [contextMenuMail, setContextMenuMail] = useState(null);
  const [menuPosition, setMenuPosition] = useState(null);

  const handleRightClick = (e, mail) => {
    e.preventDefault();
    setContextMenuMail(mail);
    setMenuPosition({ x: e.pageX, y: e.pageY });
  };

  const handleToggleLabel = (labelId) => {
    if (contextMenuMail) {
      onToggleLabel(contextMenuMail.id, labelId);
    }
  };

  return (
    <div>
      {mails.map((mail) => (
        <div key={mail.id} onContextMenu={(e) => handleRightClick(e, mail)}>
          <MailItem
            mail={mail}
            allLabels={allLabels}
            onDelete={onDelete}
            onStarToggle={onStarToggle}
            onToggleLabel={onToggleLabel}
            onClick={onClick}
            onSpam={onSpam}
          />
        </div>
      ))}

      {menuPosition && contextMenuMail && (
        <LabelContextMenu
          labels={allLabels}
          selectedLabelIds={contextMenuMail.labelIds || []}
          onToggleLabel={handleToggleLabel}
          position={menuPosition}
          onClose={() => setMenuPosition(null)}
        />
      )}
    </div>
  );
};

export default MailList;
