import React from 'react';
import './ContextMenu.css';

const ContextMenu = ({ items, onClose }) => {
  return (
    <div className="context-menu">
      <ul className="menu-list">
        {items.map((item, i) => (
          item.type === 'divider' ? (
            <hr key={i} className="menu-divider" />
          ) : (
            <li key={i} className="menu-item" onClick={item.onClick}>
              <span className="menu-icon">{item.icon}</span>
              <span className="menu-label">{item.label}</span>
            </li>
          )
        ))}
      </ul>
    </div>
  );
};

export default ContextMenu;
