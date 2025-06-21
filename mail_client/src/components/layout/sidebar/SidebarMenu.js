import { useState } from 'react';
import { ComposeButton } from "../../common/button/IconTextButton";
import SidebarItem from './SidebarItem';
import {
  MdInbox,
  MdOutlineInbox,
  MdStar,
  MdStarBorder,
  MdSend,
  MdOutlineSend,
  MdOutlineInsertDriveFile,
  MdInsertDriveFile,
  MdMail,
  MdOutlineMail,
  MdReport,
  MdOutlineReport,
} from 'react-icons/md';
import './SidebarMenu.css'

/**
 * SidebarMenu component – displays the full sidebar menu with interactive items
 */
const SidebarMenu = () => {
  const [activeItem, setActiveItem] = useState('inbox');

  const items = [
    { id: 'inbox', label: 'Inbox', filledIcon: <MdInbox />, outlineIcon: <MdOutlineInbox /> },
    { id: 'starred', label: 'Starred', filledIcon: <MdStar />, outlineIcon: <MdStarBorder /> },
    { id: 'sent', label: 'Sent', filledIcon: <MdSend />, outlineIcon: <MdOutlineSend /> },
    { id: 'drafts', label: 'Drafts', filledIcon: <MdInsertDriveFile />, outlineIcon: <MdOutlineInsertDriveFile /> },
    { id: 'allmail', label: 'All Mail', filledIcon: <MdMail />, outlineIcon: <MdOutlineMail /> },
    { id: 'spam', label: 'Spam', filledIcon: <MdReport />, outlineIcon: <MdOutlineReport /> },
  ];

  return (
    <div className="sidebar-menu">
      {/* Compose button */}
      <ComposeButton onClick={() => console.log("Compose clicked")} />
      
      {/* Sidebar items */}
      {items.map(({ id, label, filledIcon, outlineIcon }) => (
        <SidebarItem
          key={id}
          icon={activeItem === id ? filledIcon : outlineIcon}
          label={label}
          active={activeItem === id}
          onClick={() => setActiveItem(id)}
        />
      ))}
    </div>
  );
};

export default SidebarMenu;
