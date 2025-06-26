import "./TextButton.css";

/**
 * TextButton component.
 * A button with optional icon and variant (primary, ghost).
 *
 * Props:
 * - children: button text
 * - onClick: function – click handler
 * - variant: "primary" | "ghost" – visual style (default: "primary")
 * - icon: JSX element – optional icon shown before the text
 * - className: string – optional additional classes
 * - disabled: boolean – if true, button is disabled
 */
const TextButton = ({ children, onClick, variant = "primary", icon = null, className = "", disabled = false }) => {
  const finalClass = `btn ${variant} ${disabled ? "disabled" : ""} ${className}`.trim();

  return (
    <button onClick={onClick} className={finalClass} disabled={disabled}>
      {icon && <span className="btn-icon">{icon}</span>}
      {children}
    </button>
  );
};

export default TextButton;
