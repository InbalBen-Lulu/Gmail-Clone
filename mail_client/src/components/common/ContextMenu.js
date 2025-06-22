import './ContextMenu.css';

/**
 * ContextMenu – renders a dropdown menu with custom items.
 *
 * Props:
 * - items: array of menu items or dividers
 *   Each item: { label: string, onClick: function, icon?: JSX } or { type: 'divider' }
 * - onClose: optional function to call when the menu should close
 */
const ContextMenu = ({ items, onClose }) => {
  return (
    <div className="context-menu">
      <ul className="menu-list">
        {items.map((item, i) => (
          item.type === 'divider' ? (
            <hr key={i} className="menu-divider" />
          ) : (
            <li key={i} className="menu-item" onClick={item.onClick}>
              {/* Render icon only if provided */}
              {item.icon && <span className="menu-icon">{item.icon}</span>}
              <span className="menu-label">{item.label}</span>
            </li>
          )
        ))}
      </ul>
    </div>
  );
};

export default ContextMenu;
