import './SidebarItem.css';

/**
 * SidebarItem component – represents a single clickable sidebar item
 *
 * Props:
 * - icon: React element (e.g., from react-icons)
 * - label: string
 * - active: boolean
 * - onClick: function
 */
const SidebarItem = ({ icon, label, active, onClick }) => {
  return (
    <div
      className={`sidebar-item ${active ? 'active' : ''}`}
      onClick={onClick}
      role="button"
      tabIndex={0}
    >
      <div className="sidebar-icon">{icon}</div>
      <div className="sidebar-label">{label}</div>
    </div>
  );
};

export default SidebarItem;
