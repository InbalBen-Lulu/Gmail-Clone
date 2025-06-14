import { useState } from 'react';
import './DropdownList.css';
import { DROPDOWN_VARIANTS } from './constants';

/**
 * Reusable Dropdown (select) component with styling variants and floating labels.
 */
const DropdownList = ({
  label, // Label for the dropdown (optional, especially useful for floating variant)
  name, // Input name and id
  value, // Current selected value
  onChange, // Callback for value change
  options = [], // List of options: { value, label }
  size = 'md', // Size class
  variant = DROPDOWN_VARIANTS.FLOATING // Dropdown style variant
}) => {
  const [focused, setFocused] = useState(false);
  const hasValue = value && value.length > 0;

  return (
    <div className={`dropdown-wrapper ${size} ${variant} ${focused ? 'focused' : ''} ${hasValue ? 'filled' : ''}`}>
      {label && variant === DROPDOWN_VARIANTS.FLOATING && (
        <label htmlFor={name} className="dropdown-floating-label">{label}</label>
      )}
      <select
        name={name}
        id={name}
        value={value}
        onChange={onChange}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        className="dropdown-select"
      >
        {options.map((opt) => (
          <option key={opt.value} value={opt.value} disabled={opt.value === '' && hasValue}>{opt.label}</option>
        ))}
      </select>
    </div>
  );
};

export default DropdownList;

