import "./BaseButton.css";

/**
 * Base button component used by all button types.
 */
const BaseButton = ({
  variant = "primary",
  size = "md",
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
      className={`btn ${variant} ${size} ${className}`.trim()}
      {...rest}
    >
      {children}
    </button>
  );
};

export default BaseButton;
