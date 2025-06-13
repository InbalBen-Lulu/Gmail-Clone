import React, { useState } from 'react';
import './DropdownList.css';
import { DROPDOWN_VARIANTS } from './constants';

const DropdownList = ({
  label,
  name,
  value,
  onChange,
  options = [],
  size = 'md',
  variant = DROPDOWN_VARIANTS.FLOATING
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

