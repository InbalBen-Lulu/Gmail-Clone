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
const LabelContextMenu = ({ onEdit, onRemove, onSetColor, onClose, contextRef }) => {
  const [showColorSubmenu, setShowColorSubmenu] = useState(false);
  const wrapperRef = useRef(null);

  // Close context menu only if user clicks outside both the menu and the 3-dot icon button
  useEffect(() => {
    const handleClickOutside = (e) => {
      const clickedOutsideMenu =
        wrapperRef.current && !wrapperRef.current.contains(e.target);
      const clickedOutsideButton =
        contextRef?.current && !contextRef.current.contains(e.target);

      if (clickedOutsideMenu && clickedOutsideButton) {
        onClose(); // Close only if click is outside both
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose, contextRef]);

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
