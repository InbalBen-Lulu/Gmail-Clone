import BaseButton from "./BaseButton";
import "./IconButtons.css";

/**
 * Base icon-only button.
 * Props:
 * - icon: JSX element (e.g. <MdSettings />)
 * - ariaLabel: for accessibility (required)
 * - size: "small" | "large"
 * - className: additional CSS classes
 */
const IconButton = ({
  icon,
  ariaLabel,
  size = "small",
  className = "",
  disabled = false,
  ...rest
}) => {
  const sizeClass = size === "large" ? "icon-large" : "icon-small";
  const disabledClass = disabled ? "disabled" : "";

  const finalClassName = `btn icon ${sizeClass} ${disabledClass} ${className}`.trim();

  return (
    <BaseButton
      variant="icon"
      aria-label={ariaLabel}
      className={finalClassName}
      disabled={disabled}
      {...rest}
    >
      {icon}
    </BaseButton>
  );
};

export default IconButton;
