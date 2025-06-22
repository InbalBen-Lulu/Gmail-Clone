

// import { useState, useRef, useEffect } from "react";
// import { useLabelService } from '../../services/useLabelService';
// import { MdLabel } from "react-icons/md";
// import {SmallIconButton} from "../common/button/IconButtons";
// import Icon from "../../assets/icons/Icon";
// import LabelDialog from "./LabelDialog";
// import LabelContextMenu from "./LabelContextMenu";
// import "./LabelItem.css";

// const LabelItem = ({ label, color, isActive, onClick }) => {
//   const [showMenu, setShowMenu] = useState(false);
//   const [showEditDialog, setShowEditDialog] = useState(false);
//   const menuRef = useRef(null);
//   const { createLabel } = useLabelService();
//   const closeMenu = () => setShowMenu(false);
  
//   const handleMoreClick = (e) => {
//     e.stopPropagation();
//     setShowMenu((prev) => !prev);
//   };

//   useEffect(() => {
//     const handleClickOutside = (e) => {
//       if (menuRef.current && !menuRef.current.contains(e.target)) {
//         closeMenu();
//       }
//     };

//     if (showMenu) {
//       document.addEventListener("mousedown", handleClickOutside);
//     } else {
//       document.removeEventListener("mousedown", handleClickOutside);
//     }

//     return () => document.removeEventListener("mousedown", handleClickOutside);
//   }, [showMenu]);

//   const handleEdit = () => {
//     closeMenu();
//     setShowEditDialog(true);
//   };

//   const handleRemove = () => {
//     closeMenu();
//     console.log("Remove label:", label);
//   };

//   const handleSetColor = (newColor) => {
//     closeMenu();
//     console.log("Set color for", label, "=>", newColor || "none");
//     // TODO: עדכון צבע בלייבלים כשנחבר לשרת
//   };

//   return (
//     <>
//       <div className={`label-item ${isActive ? "active" : ""}`} onClick={onClick}>
//         <MdLabel className="label-icon" style={{ color }} />
//         <span className="label-name">{label}</span>

//         <div className="more-button-wrapper" ref={menuRef}>
//           <SmallIconButton
//             icon={<Icon name="more" />}
//             ariaLabel="More options"
//             size="small"
//             className="more-button"
//             onClick={handleMoreClick}
//           />
//           {showMenu && (
//             <LabelContextMenu
//               onEdit={handleEdit}
//               onRemove={handleRemove}
//               onSetColor={handleSetColor}
//               onClose={closeMenu}
//             />
//           )}
//         </div>
//       </div>

//       {showEditDialog && (
//         <LabelDialog
//           mode="edit"
//           initialName={label}
//           onCancel={() => setShowEditDialog(false)}
//           onCreate={(newName) => {
//             console.log("Saved label as:", newName);
//             setShowEditDialog(false);
//           }}
//         />
//       )}
//     </>
//   );
// };

// export default LabelItem;
import { useState, useRef, useEffect } from "react";
import { MdLabel } from "react-icons/md";
import { SmallIconButton } from "../common/button/IconButtons";
import Icon from "../../assets/icons/Icon";
import LabelDialog from "./LabelDialog";
import LabelContextMenu from "./LabelContextMenu";
import { useLabelService } from "../../services/useLabelService";
import { useLabels } from "../../contexts/LabelContext";
import "./LabelItem.css";

const LabelItem = ({ label, color, id, isActive, onClick }) => {
  const [showMenu, setShowMenu] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const menuRef = useRef(null);

  const { renameLabel, deleteLabel, setLabelColor, resetLabelColor } = useLabelService();
  const { refreshLabels } = useLabels();

  const handleMoreClick = (e) => {
    e.stopPropagation();
    setShowMenu((prev) => !prev);
  };

  const closeMenu = () => setShowMenu(false);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        closeMenu();
      }
    };

    if (showMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showMenu]);

  const handleEdit = () => {
    closeMenu();
    setShowEditDialog(true);
  };

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

  const handleRemove = async () => {
    closeMenu();
    try {
      await deleteLabel(id);
      await refreshLabels();
    } catch (err) {
      console.error("Failed to delete label:", err.message);
    }
  };

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
      <div className={`label-item ${isActive ? "active" : ""}`} onClick={onClick}>
        <MdLabel className="label-icon" style={{ color }} />
        <span className="label-name">{label}</span>

        <div className="more-button-wrapper" ref={menuRef}>
          <SmallIconButton
            icon={<Icon name="more" />}
            ariaLabel="More options"
            size="small"
            className="more-button"
            onClick={handleMoreClick}
          />
          {showMenu && (
            <LabelContextMenu
              onEdit={handleEdit}
              onRemove={handleRemove}
              onSetColor={handleSetColor}
              onClose={closeMenu}
            />
          )}
        </div>
      </div>

      {showEditDialog && (
        <LabelDialog
          mode="edit"
          initialName={label}
          onCancel={() => setShowEditDialog(false)}
          onCreate={handleRename}
        />
      )}
    </>
  );
};

export default LabelItem;
