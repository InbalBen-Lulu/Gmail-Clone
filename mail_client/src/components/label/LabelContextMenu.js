// import { useState, useRef, useEffect } from "react";
// import ContextMenu from "../common/ContextMenu";
// import { LABEL_COLORS } from "./labelColors";
// import "./LabelContextMenu.css"; // רק עבור סגנון של תת-תפריט

// /**
//  * LabelContextMenu – תפריט מותאם אישית עם תת־תפריט לצבעים
//  * 
//  * Props:
//  * - onEdit: function
//  * - onRemove: function
//  * - onSetColor: function (color: string|null)
//  * - onClose: function
//  */
// const LabelContextMenu = ({ onEdit, onRemove, onSetColor, onClose }) => {
//   const [showColorSubmenu, setShowColorSubmenu] = useState(false);
//   const submenuRef = useRef(null);

//   useEffect(() => {
//     const handleClickOutside = (e) => {
//       if (submenuRef.current && !submenuRef.current.contains(e.target)) {
//         setShowColorSubmenu(false);
//       }
//     };

//     if (showColorSubmenu) {
//       document.addEventListener("mousedown", handleClickOutside);
//     }

//     return () => document.removeEventListener("mousedown", handleClickOutside);
//   }, [showColorSubmenu]);

//   const items = [
//     { label: "Edit", onClick: onEdit },
//     { label: "Remove label", onClick: onRemove },
//     {
//       label: (
//         <div className="submenu-wrapper" onMouseEnter={() => setShowColorSubmenu(true)}>
//           <span>Label color</span>
//           <span className="submenu-arrow">▶</span>

//           {/* Submenu */}
//           {showColorSubmenu && (
//             <div className="color-submenu" ref={submenuRef}>
//               <div className="label-color-grid">
//                 {LABEL_COLORS.map((color, i) => (
//                   <div
//                     key={i}
//                     className="color-circle"
//                     style={{ backgroundColor: color }}
//                     onClick={() => {
//                       onSetColor(color);
//                       onClose();
//                     }}
//                   />
//                 ))}
//               </div>
//               <div
//                 className="remove-color-option"
//                 onClick={() => {
//                   onSetColor(null);
//                   onClose();
//                 }}
//               >
//                 Remove color
//               </div>
//             </div>
//           )}
//         </div>
//       ),
//       onClick: () => {}, // prevent close
//     }
//   ];

//   return <ContextMenu items={items} onClose={onClose} />;
// };

// export default LabelContextMenu;

import { useState, useRef, useEffect } from "react";
import ContextMenu from "../common/ContextMenu";
import { LABEL_COLORS } from "./labelColors";
import "./LabelContextMenu.css"; // כולל סגנון של תת־תפריט

/**
 * LabelContextMenu – תפריט תווית עם תת־תפריט לצבעים
 *
 * Props:
 * - onEdit: function
 * - onRemove: function
 * - onSetColor: function(color: string|null)
 * - onClose: function
 */
const LabelContextMenu = ({ onEdit, onRemove, onSetColor, onClose }) => {
  const [showColorSubmenu, setShowColorSubmenu] = useState(false);
  const submenuRef = useRef(null);

  // סגירת תת־תפריט בלחיצה מחוץ
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (submenuRef.current && !submenuRef.current.contains(e.target)) {
        setShowColorSubmenu(false);
      }
    };

    if (showColorSubmenu) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showColorSubmenu]);

  const renderColorSubmenu = () => (
    <div
      className="color-submenu"
      ref={submenuRef}
      onMouseLeave={() => setShowColorSubmenu(false)}
    >
      <div className="label-color-grid">
        {LABEL_COLORS.map((color, i) => (
          <div
            key={i}
            className="color-circle"
            style={{ backgroundColor: color }}
            onClick={() => {
              onSetColor(color);
              onClose();
            }}
          />
        ))}
      </div>
      <div
        className="remove-color-option"
        onClick={() => {
          onSetColor(null);
          onClose();
        }}
      >
        Remove color
      </div>
    </div>
  );

  const items = [
    { label: "Edit", onClick: onEdit },
    { label: "Remove label", onClick: onRemove },
    {
      label: (
        <div
          className="submenu-wrapper"
          onMouseEnter={() => setShowColorSubmenu(true)}
        >
          <span>Label color</span>
          <span className="submenu-arrow">▶</span>
          {showColorSubmenu && renderColorSubmenu()}
        </div>
      ),
      onClick: () => {}, // כדי למנוע סגירה אוטומטית
    },
  ];

  return <ContextMenu items={items} onClose={onClose} />;
};

export default LabelContextMenu;
