import Icon from "../../assets/icons/Icon";
import "./Checkbox.css";

/**
 * Custom checkbox component.
 * Supports variants via CSS: 'primary' (filled) and 'minimal' (border only).
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