// import { useState } from 'react';
// import { ComposeButton } from "../../common/button/IconTextButton";
// import SidebarItem from './SidebarItem';
// import Icon from '../../../assets/icons/Icon';
// import LabelDialog from '../../label/LabelDialog';
// import { MainIconButton } from '../../common/button/IconButtons';
// import {
//   MdInbox,
//   MdOutlineInbox,
//   MdStar,
//   MdStarBorder,
//   MdSend,
//   MdOutlineSend,
//   MdOutlineInsertDriveFile,
//   MdInsertDriveFile,
//   MdMail,
//   MdOutlineMail,
//   MdReport,
//   MdOutlineReport,
// } from 'react-icons/md';
// import './SidebarMenu.css';

// /**
//  * SidebarMenu component – displays the full sidebar menu with interactive items
//  */
// const SidebarMenu = () => {
//   const [activeItem, setActiveItem] = useState('inbox');
//   const [showDialog, setShowDialog] = useState(false);

//   const items = [
//     { id: 'inbox', label: 'Inbox', filledIcon: <MdInbox />, outlineIcon: <MdOutlineInbox /> },
//     { id: 'starred', label: 'Starred', filledIcon: <MdStar />, outlineIcon: <MdStarBorder /> },
//     { id: 'sent', label: 'Sent', filledIcon: <MdSend />, outlineIcon: <MdOutlineSend /> },
//     { id: 'drafts', label: 'Drafts', filledIcon: <MdInsertDriveFile />, outlineIcon: <MdOutlineInsertDriveFile /> },
//     { id: 'allmail', label: 'All Mail', filledIcon: <MdMail />, outlineIcon: <MdOutlineMail /> },
//     { id: 'spam', label: 'Spam', filledIcon: <MdReport />, outlineIcon: <MdOutlineReport /> },
//   ];

//   return (
//     <div className="sidebar-menu">
//       {/* Compose button */}
//       <ComposeButton onClick={() => console.log("Compose clicked")} />
      
//       {/* Sidebar items */}
//       {items.map(({ id, label, filledIcon, outlineIcon }) => (
//         <SidebarItem
//           key={id}
//           icon={activeItem === id ? filledIcon : outlineIcon}
//           label={label}
//           active={activeItem === id}
//           onClick={() => setActiveItem(id)}
//         />
//       ))}

//       {/* Labels section header */}
//       <div className="labels-header">
//         <span className="labels-title">Labels</span>
//         <MainIconButton
//           icon={<Icon name="add" />}
//           ariaLabel="Add label"
//           onClick={() => setShowDialog(true)}
//         />
//       </div>

//       {/* LabelDialog for new label creation */}
//       {showDialog && (
//         <LabelDialog
//           mode="new"
//           initialName=""
//           onCancel={() => setShowDialog(false)}
//           onCreate={(labelName) => {
//             console.log("Create label:", labelName);
//             setShowDialog(false);
//           }}
//         />
//       )}
//     </div>
//   );
// };

// export default SidebarMenu;
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ComposeButton } from "../../common/button/IconTextButton";
import SidebarItem from './SidebarItem';
import Icon from '../../../assets/icons/Icon';
import LabelDialog from '../../label/LabelDialog';
import { MainIconButton } from '../../common/button/IconButtons';
import { useLabelService } from '../../../services/useLabelService';
import {
  MdInbox, MdOutlineInbox,
  MdStar, MdStarBorder,
  MdSend, MdOutlineSend,
  MdOutlineInsertDriveFile, MdInsertDriveFile,
  MdMail, MdOutlineMail,
  MdReport, MdOutlineReport
} from 'react-icons/md';
import './SidebarMenu.css';

const SidebarMenu = () => {
  const [activeItem, setActiveItem] = useState('inbox');
  const [showDialog, setShowDialog] = useState(false);
  const [labels, setLabels] = useState([]);
  const navigate = useNavigate();
  const labelService = useLabelService();

  const items = [
    { id: 'inbox', label: 'Inbox', filledIcon: <MdInbox />, outlineIcon: <MdOutlineInbox /> },
    { id: 'starred', label: 'Starred', filledIcon: <MdStar />, outlineIcon: <MdStarBorder /> },
    { id: 'sent', label: 'Sent', filledIcon: <MdSend />, outlineIcon: <MdOutlineSend /> },
    { id: 'drafts', label: 'Drafts', filledIcon: <MdInsertDriveFile />, outlineIcon: <MdOutlineInsertDriveFile /> },
    { id: 'allmail', label: 'All Mail', filledIcon: <MdMail />, outlineIcon: <MdOutlineMail /> },
    { id: 'spam', label: 'Spam', filledIcon: <MdReport />, outlineIcon: <MdOutlineReport /> },
  ];

  // Load user labels on mount
  useEffect(() => {
    labelService.fetchLabels()
      .then(setLabels)
      .catch((err) => console.error("Error fetching labels:", err));
  }, []);

  const handleClickItem = (id) => {
    setActiveItem(id);
    navigate(`/mails/${id}`);
  };

  const handleClickLabel = (label) => {
    setActiveItem(`label-${label.id}`);
    navigate(`/mails/label-${label.id}`);
  };

  return (
    <div className="sidebar-menu">
      <ComposeButton onClick={() => console.log("Compose clicked")} />

      {/* Static items */}
      {items.map(({ id, label, filledIcon, outlineIcon }) => (
        <SidebarItem
          key={id}
          icon={activeItem === id ? filledIcon : outlineIcon}
          label={label}
          active={activeItem === id}
          onClick={() => handleClickItem(id)}
        />
      ))}

      {/* Labels header */}
      <div className="labels-header">
        <span className="labels-title">Labels</span>
        <MainIconButton
          icon={<Icon name="add" />}
          ariaLabel="Add label"
          onClick={() => setShowDialog(true)}
        />
      </div>

      {/* User-defined labels */}
      {labels.map((label) => (
        <SidebarItem
          key={label.id}
          icon={<span className="label-icon" style={{ color: label.color || 'gray' }}>●</span>}
          label={label.name}
          active={activeItem === `label-${label.id}`}
          onClick={() => handleClickLabel(label)}
        />
      ))}

      {/* Dialog to create new label */}
      {showDialog && (
        <LabelDialog
          mode="new"
          initialName=""
          onCancel={() => setShowDialog(false)}
          onCreate={async (name) => {
            try {
              await labelService.createLabel(name);
              const updated = await labelService.fetchLabels();
              setLabels(updated);
            } catch (err) {
              alert("Failed to create label");
              console.error(err);
            }
            setShowDialog(false);
          }}
        />
      )}
    </div>
  );
};

export default SidebarMenu;
