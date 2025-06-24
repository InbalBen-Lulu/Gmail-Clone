import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ComposeButton } from "../../common/button/IconTextButton";
import SidebarItem from './SidebarItem';
import Icon from '../../../assets/icons/Icon';
import LabelDialog from '../../label/LabelDialog';
import LabelItem from '../../label/LabelItem'
import { MainIconButton } from '../../common/button/IconButtons';
import { useLabelService } from '../../../services/useLabelService';
import { useCompose } from '../../../contexts/ComposeContext'; 
import { useLabels } from '../../../contexts/LabelContext';
import {
  MdInbox, MdOutlineInbox,
  MdStar, MdStarBorder,
  MdSend, MdOutlineSend,
  MdOutlineInsertDriveFile, MdInsertDriveFile,
  MdMail, MdOutlineMail,
  MdReport, MdOutlineReport
} from 'react-icons/md';
import './SidebarMenu.css';

/**
 * SidebarMenu component – Gmail-style sidebar with navigation and label management.
 * Displays static mail categories, user-created labels, and a compose button.
 */
const SidebarMenu = () => {
  const [activeItem, setActiveItem] = useState('inbox');     
  const [showDialog, setShowDialog] = useState(false);                        
  const [labelError, setLabelError] = useState('');

  const navigate = useNavigate();                            
  const location = useLocation();                            
  const labelService = useLabelService();
  const { labels, refreshLabels } = useLabels();                    
  const { openCompose, showCompose } = useCompose();

  // Static built-in categories with icon sets (filled/outline)
  const items = [
    { id: 'inbox', label: 'Inbox', filledIcon: <MdInbox />, outlineIcon: <MdOutlineInbox /> },
    { id: 'starred', label: 'Starred', filledIcon: <MdStar />, outlineIcon: <MdStarBorder /> },
    { id: 'sent', label: 'Sent', filledIcon: <MdSend />, outlineIcon: <MdOutlineSend /> },
    { id: 'drafts', label: 'Drafts', filledIcon: <MdInsertDriveFile />, outlineIcon: <MdOutlineInsertDriveFile /> },
    { id: 'allmail', label: 'All Mail', filledIcon: <MdMail />, outlineIcon: <MdOutlineMail /> },
    { id: 'spam', label: 'Spam', filledIcon: <MdReport />, outlineIcon: <MdOutlineReport /> },
  ];

  // Load labels on first mount
  useEffect(() => {
    refreshLabels();
  }, [refreshLabels]);

  /**
   * Automatically update the selected sidebar item based on the current URL path.
   * Ensures correct highlight even on page reload or direct navigation.
   */
  useEffect(() => {
    const path = location.pathname;
    if (path.startsWith('/mails/labels-')) {
      setActiveItem(path.replace('/mails/', '')); // e.g., "label-3"
    } else if (path.startsWith('/mails/')) {
      setActiveItem(path.replace('/mails/', '')); // e.g., "inbox"
    }
  }, [location]);

  /**
   * Navigate to a built-in mail category (e.g., inbox, sent).
   * @param {string} id - Category ID
   */
  const handleClickItem = (id) => {
    navigate(`/mails/${id}`);
  };

  /**
   * Navigate to a specific user-created label.
   * @param {object} label - Label object with id and name
   */
  const handleClickLabel = (label) => {
    setActiveItem(`label-${label.id}`); 
    navigate(`/mails/labels-${label.id}`);
  };

  /**
   * Handle the creation of a new label.
   * Calls label service to create the label and handles validation errors.
   *
   * @param {string} name - The name of the new label to create
   */ 
  const handleCreateLabel = async (name) => {
    try {
      await labelService.createLabel(name);
      await refreshLabels();
      setLabelError('');
      setShowDialog(false);
    } catch (err) {
      if (err.message.includes('already exists')) {
        setLabelError('The label name you have chosen already exists. Please try another name');
      } else {
        setLabelError('Failed to create label.');
      }
    }
  };

  return (
    <div className="sidebar-menu">
      {/* Compose mail button */}
      <ComposeButton
        onClick={() => openCompose()}
        disabled={showCompose}
      />

      {/* Static mail category items */}
      {items.map(({ id, label, filledIcon, outlineIcon }) => (
        <SidebarItem
          key={id}
          icon={activeItem === id ? filledIcon : outlineIcon}
          label={label}
          active={activeItem === id}
          onClick={() => handleClickItem(id)}
        />
      ))}

      {/* Header for user labels */}
      <div className="labels-header">
        <span className="labels-title">Labels</span>
        <MainIconButton
          icon={<Icon name="add" />}
          ariaLabel="Add label"
          onClick={() => setShowDialog(true)}
        />
      </div>

      {/* Custom user-created labels */}
      {labels.map((label) => (
        <LabelItem
          key={label.id}
          id={label.id}
          label={label.name}
          color={label.color}
          isActive={activeItem === `label-${label.id}`}
          onClick={() => handleClickLabel(label)}
        />
      ))}

      {/* Dialog to create a new label */}
      {showDialog && (
        <LabelDialog
          mode="new"
          initialName=""
          existingLabels={labels.map((l) => l.name)}
          onCancel={() => {
            setShowDialog(false);
            setLabelError('');
          }}
          onCreate={handleCreateLabel}
          externalError={labelError}
          clearExternalError={() => setLabelError('')}
        />
      )}
    </div>
  );
};

export default SidebarMenu;

