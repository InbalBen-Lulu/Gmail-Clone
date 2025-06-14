// import BaseButton from "./BaseButton";
// import "./IconButton.css";
// import { MdStar, MdStarBorder } from "react-icons/md";

// /**
//  * General-purpose icon-only button.
//  * Props:
//  * - icon: React component (JSX) – required
//  * - ariaLabel: string – for accessibility
//  * - size: "small" | "large" (default: "small")
//  */
// const IconButton = ({ icon: Icon, ariaLabel, size = "small", className = "", ...rest }) => {
//   const sizeClass = size === "large" ? "icon-large" : "icon-small";
//   const iconSize = size === "large" ? 24 : 18;

//   const finalClassName = `btn icon ${sizeClass} ${className}`.trim();

//   return (
//     <BaseButton
//       variant="icon"
//       aria-label={ariaLabel}
//       className={finalClassName}
//       {...rest}
//     >
//       <Icon size={iconSize} className="icon" />
//     </BaseButton>
//   );
// };

// export default IconButton;

// export const StarButton = ({ isStarred, onClick, className = "" }) => {
//   const IconComponent = isStarred ? MdStar : MdStarBorder;

//   return (
//     <button
//       onClick={onClick}
//       aria-label={isStarred ? "Unstar" : "Star"}
//       className={`btn icon star-button ${className}`}
//     >
//       <IconComponent size={24} className={`star-icon ${isStarred ? "starred" : ""}`} />
//     </button>
//   );
// };
import React from "react";
import BaseButton from "./BaseButton";
import "./IconButton.css";
import { MdStar, MdStarBorder } from "react-icons/md";

/**
 * General-purpose icon-only button.
 * Props:
 * - icon: JSX element (e.g. <MdSettings />)
 * - ariaLabel: string – for accessibility
 * - size: "small" | "large" (default: "small")
 */
const IconButton = ({ icon, ariaLabel, size = "small", className = "", ...rest }) => {
  const sizeClass = size === "large" ? "icon-large" : "icon-small";
  const iconSize = size === "large" ? 24 : 18;

  // Clone the icon element to inject size and class
  const sizedIcon = React.cloneElement(icon, {
    size: iconSize,
    className: "icon"
  });

  const finalClassName = `btn icon ${sizeClass} ${className}`.trim();

  return (
    <BaseButton
      variant="icon"
      aria-label={ariaLabel}
      className={finalClassName}
      {...rest}
    >
      {sizedIcon}
    </BaseButton>
  );
};

export default IconButton;

/**
 * Specific implementation for star/favorite button.
 * Uses fixed size and dynamic fill color based on state.
 */
export const StarButton = ({ isStarred, onClick, className = "" }) => {
  const IconComponent = isStarred ? MdStar : MdStarBorder;

  return (
    <button
      onClick={onClick}
      aria-label={isStarred ? "Unstar" : "Star"}
      className={`btn icon icon-small star-button ${className}`.trim()}
    >
      <IconComponent size={18} className={`star-icon ${isStarred ? "starred" : ""}`} />
    </button>
  );
};
