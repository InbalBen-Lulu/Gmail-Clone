import "./TextButton.css";

/**
 * Text button, optionally with icon
 * Props:
 * - icon: optional icon shown before the text
 * - children: button label
 */
const TextButton = ({ children, onClick, variant = "primary", icon = null, className = "" }) => {
  const finalClass = `btn ${variant} ${className}`.trim();

  return (
    <button onClick={onClick} className={finalClass}>
      {icon && <span className="btn-icon">{icon}</span>}
      {children}
    </button>
  );
};


export default TextButton;
