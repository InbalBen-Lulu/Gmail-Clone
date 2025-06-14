import React from "react";
import "./BaseButton.css";

/**
 * Base button component used internally by other buttons.
 * Props:
 * - variant: button style variant (e.g., "primary", "ghost", "icon")
 * - size: button size ("sm", "md", "lg")
 * - disabled: whether the button is disabled
 * - onClick: click handler
 * - type: HTML button type
 * - className: optional extra class
 * - children: contents inside the button
 */
const BaseButton = ({
  variant = "primary",
  size = "md",
  disabled = false,
  onClick,
  type = "button",
  className = "",
  children,
  ...rest
}) => {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`btn ${variant} ${size} ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
};

export default BaseButton;