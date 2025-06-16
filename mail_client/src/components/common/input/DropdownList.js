import { useState } from 'react';
import './DropdownList.css';
import ErrorMessage from './ErrorMessage';

/**
 * Reusable Dropdown (select) component with styling variants and floating labels.
 */
const DropdownList = ({
  label,
  name,
  value,
  onChange,
  options = [],
  size = 'md',
  variant = 'floating',
  isInvalid = false,
  isValid = false,
  errorMessage = ''
}) => {
  const [focused, setFocused] = useState(false);
  const hasValue = value && value !== '';

  const wrapperClass = `
    dropdown-wrapper ${size} ${variant}
    ${focused ? 'focused' : ''}
    ${hasValue ? 'filled' : ''}
    ${isInvalid ? 'invalid' : ''}
    ${!isInvalid && isValid && hasValue ? 'valid' : ''}
  `.trim();

  return (
    <div className={wrapperClass}>
      {label && variant === 'floating' && (focused || value) && (
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
        {options.map((opt, index) => (
          <option
            key={opt.value}
            value={opt.value}
            disabled={opt.value === ''}
            hidden={opt.value === '' && index === 0}
          >
            {opt.label}
          </option>
        ))}
      </select>
      {/* Show error message *below* the wrapper */}
      {isInvalid && errorMessage.trim() !== '' && (
        <ErrorMessage message={errorMessage} />
      )}
    </div>

  );
};

export default DropdownList;
