import Icon from "../../assets/icons/Icon";
import "./Checkbox.css";

/**
 * Custom checkbox component.
 * Rendered as a button with role="checkbox".
 * Shows a checkmark icon (✓) when checked.
 * Toggles state via onClick and applies "checked" class for styling.
 */
const Checkbox = ({ checked, onChange, className = "" }) => {
  const finalClassName = `checkbox ${checked ? "checked" : ""} ${className}`.trim();

  return (
    <button
      type="button"
      role="checkbox"
      aria-checked={checked}
      onClick={() => onChange(!checked)}
      className={finalClassName}
    >
      {checked && <Icon name="check" className="checkbox-icon" />}
    </button>
  );
};

export default Checkbox;
