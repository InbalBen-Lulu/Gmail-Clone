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
  ...rest
}) => {
  const sizeClass = size === "large" ? "icon-large" : "icon-small";

  const finalClassName = `btn icon ${sizeClass} ${className}`.trim();

  return (
    <BaseButton
      variant="icon"
      aria-label={ariaLabel}
      className={finalClassName}
      {...rest}
    >
      {icon}
    </BaseButton>
  );
};

export default IconButton;
