import React, { useRef, useEffect } from 'react';
import './ContextMenu.css';

const ContextMenu = ({ items, onClose }) => {
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        onClose();
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
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
            <li
              key={i}
              className="menu-item"
              onClick={item.type === 'checkbox' ? undefined : item.onClick}
            >
              {item.type === 'checkbox' ? (
                <label className="menu-checkbox-wrapper">
                  <input
                    type="checkbox"
                    checked={item.checked}
                    onChange={item.onClick}
                    className="menu-checkbox"
                  />
                  <span className="menu-label">{item.label}</span>
                </label>
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
