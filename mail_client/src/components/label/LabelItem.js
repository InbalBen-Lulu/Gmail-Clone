import { useState, useRef} from "react";
import { MdLabel } from "react-icons/md";
import { SmallIconButton } from "../common/button/IconButtons";
import Icon from "../../assets/icons/Icon";
import LabelDialog from "./LabelDialog";
import LabelContextMenu from "./label_context_menu/LabelContextMenu";
import { useLabelService } from "../../services/useLabelService";
import { useLabels } from "../../contexts/LabelContext";
import "./LabelItem.css";

/**
 * LabelItem – represents a single label item in the sidebar with edit/delete/color options.
 *
 * Props:
 * - label: string – the label name
 * - color: string – the color of the label
 * - id: number – the label ID
 * - isActive: boolean – whether this label is currently selected
 * - onClick: function – called when clicking the label item
 */
const LabelItem = ({ label, color, id, isActive, onClick }) => {
  const [showMenu, setShowMenu] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const menuRef = useRef(null);

  const { renameLabel, deleteLabel, setLabelColor, resetLabelColor } = useLabelService();
  const { labels, refreshLabels } = useLabels();

  const closeMenu = () => setShowMenu(false);

  /**
   * Toggles the visibility of the label's context menu (3-dot icon).
   * Stops event from bubbling up to prevent triggering parent click.
   */
  const handleMoreClick = (e) => {
    e.stopPropagation();
    setShowMenu((prev) => !prev);
  };

  /**
   * Opens the label edit dialog and closes the context menu.
   */
  const handleEdit = () => {
    closeMenu();
    setShowEditDialog(true);
  };

  /**
   * Handles renaming the label to a new name.
   * Calls backend service, refreshes labels, and closes the dialog.
   *
   * @param {string} newName – The updated label name
   */
  const handleRename = async (newName) => {
    try {
      await renameLabel(id, newName);
      await refreshLabels();
    } catch (err) {
      console.error("Failed to rename label:", err.message);
    } finally {
      setShowEditDialog(false);
    }
  };

  /**
   * Deletes the label from the system.
   * Closes the context menu and refreshes the label list.
   */
  const handleRemove = async () => {
    closeMenu();
    try {
      await deleteLabel(id);
      await refreshLabels();
    } catch (err) {
      console.error("Failed to delete label:", err.message);
    }
  };

  /**
   * Updates the label's color.
   * If newColor is null, the label color will be reset to default.
   *
   * @param {string|null} newColor – The new color hex code or null
   */
  const handleSetColor = async (newColor) => {
    closeMenu();
    try {
      if (newColor) {
        await setLabelColor(id, newColor);
      } else {
        await resetLabelColor(id);
      }
      await refreshLabels();
    } catch (err) {
      console.error("Failed to update label color:", err.message);
    }
  };

  return (
    <>
      <div
        className={`label-item ${isActive ? "active" : ""}`}
        onClick={() => {
          closeMenu();   // Close any open menu when label is clicked
          onClick();     // Navigate to label
        }}
      >
        <MdLabel className="label-icon" style={{ color }} />
        <span className="label-name">{label}</span>

        <div className="more-button-wrapper" ref={menuRef}>
          <SmallIconButton
            icon={<Icon name="more" />}
            ariaLabel="More options"
            size="small"
            className={`more-button ${showMenu ? "pressed" : ""}`}
            onClick={handleMoreClick}
          />

          {showMenu && (
            <LabelContextMenu
              onEdit={handleEdit}
              onRemove={handleRemove}
              onSetColor={handleSetColor}
              onClose={closeMenu}
              contextRef={menuRef}
            />
          )}
        </div>
      </div>

      {showEditDialog && (
        <LabelDialog
          mode="edit"
          initialName={label}
          existingLabels={labels
            .map((l) => l.name)
            .filter((name) => name !== label)} // prevent self-comparison
          onCancel={() => setShowEditDialog(false)}
          onCreate={handleRename}
        />
      )}
    </>
  );
};

export default LabelItem;
