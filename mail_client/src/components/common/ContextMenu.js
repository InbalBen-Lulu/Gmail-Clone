import { useRef, useEffect } from 'react';
import './ContextMenu.css';

/**
 * Generic context menu component.
 * Renders a list of menu items (checkboxes, dividers, titles, regular actions).
 * Closes automatically when clicking outside.
 */
const ContextMenu = ({ items, onClose }) => {
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => { // Close the menu if the user clicks outside of it
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        onClose();
      }
    };

    const handleScroll = () => {
      onClose();
    };
    
    document.addEventListener('mousedown', handleClickOutside);
    window.addEventListener('scroll', handleScroll, true);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      window.removeEventListener('scroll', handleScroll, true);
    };
  }, [onClose]);

  return (
    <div className="context-menu" ref={menuRef}>
      <ul className="menu-list">
        {items.map((item, i) => {
          if (item.type === 'divider') {
            return <hr key={i} className="menu-divider" />;
          }

          if (item.type === 'title') {
            return <div key={i} className="menu-title">{item.label}</div>;
          }

          return (
            <li key={i} className="menu-item">
              {item.type === 'checkbox' ? (
                <div className="menu-checkbox-wrapper">
                  <input
                    type="checkbox"
                    checked={item.checked}
                    onChange={(e) => {
                      e.stopPropagation();
                      item.onClick();
                    }}
                    className="menu-checkbox"
                    id={`checkbox-${i}`}
                  />

                  <label htmlFor={`checkbox-${i}`} className="menu-label">
                    {item.label}
                  </label>
                </div>
              ) : (
                <>
                  {item.icon && <span className="menu-icon">{item.icon}</span>}
                  <span className="menu-label">{item.label}</span>
                </>
              )}
            </li>
          );
        })}
      </ul>
    </div>
  );
};

export default ContextMenu;
