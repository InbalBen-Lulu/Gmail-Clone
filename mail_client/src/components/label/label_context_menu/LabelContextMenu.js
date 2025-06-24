import { useState, useRef, useEffect } from "react";
import SimpleContextMenu from "./SimpleContextMenu";
import { LABEL_COLORS } from "./labelColors";
import "./LabelContextMenu.css"; 

/**
 * LabelContextMenu – context menu for a label with edit/remove/color options.
 *
 * Props:
 * - onEdit: function – called when "Edit" is selected
 * - onRemove: function – called when "Remove label" is selected
 * - onSetColor: function(color: string | null) – called with selected color or null to reset
 * - onClose: function – called to close the context menu
 */
const LabelContextMenu = ({ onEdit, onRemove, onSetColor, onClose }) => {
  const [showColorSubmenu, setShowColorSubmenu] = useState(false);
  const wrapperRef = useRef(null);

  // Close menu if clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        onClose(); // Close entire menu
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose]);

  const renderColorSubmenu = () => (
    <div className="color-submenu">
      <div className="label-color-grid">
        {LABEL_COLORS.map((color, i) => (
          <div
            key={i}
            className="color-circle"
            style={{ backgroundColor: color }}
            onClick={() => {
              onSetColor(color);
              onClose();
            }}
          />
        ))}
      </div>
      <div
        className="remove-color-option"
        onClick={() => {
          onSetColor(null);
          onClose();
        }}
      >
        Remove color
      </div>
    </div>
  );

  const items = [
    { label: "Edit", onClick: onEdit },
    { label: "Remove label", onClick: onRemove },
    {
      label: (
        <div
          className="submenu-wrapper"
          onMouseEnter={() => setShowColorSubmenu(true)}
        >
          <span>Label color</span>
          <span className="submenu-arrow">▶</span>
          {showColorSubmenu && renderColorSubmenu()}
        </div>
      ),
      onClick: () => {}, // Prevent closing
    },
  ];

  return (
    <div ref={wrapperRef}>
      <SimpleContextMenu items={items} onClose={onClose} />
    </div>
  );
};

export default LabelContextMenu;
