import { useState } from 'react';
import './TextBox.css';
import { TEXTBOX_VARIANTS } from './constants';

/**
 * Reusable Textbox component with support for different variants and styles.
 */
const Textbox = ({
  label, // Label to display above or inside the input
  name, // Input name and id
  value, // Controlled value
  onChange, // Callback to update value
  maxLength, // Optional: max number of characters allowed
  placeholder = '',
  type = 'text',
  size = 'md', // Size class
  variant = TEXTBOX_VARIANTS.FLOATING,  // Input style variant
  suffix = '' // Optional: suffix to display after the input
}) => {
  const [isFocused, setIsFocused] = useState(false);
  const hasValue = value?.length > 0;
  const isTextarea = variant === TEXTBOX_VARIANTS.COMPOSE_BODY;
  const wrapperClass = `textbox-wrapper ${size} ${variant} ${isFocused ? 'focused' : ''} ${hasValue ? 'filled' : ''}`;


  return (
    <div className={wrapperClass}>
      {isTextarea ? (
        <>
          <textarea
            id={name}
            name={name}
            value={value}
            onChange={onChange}
            maxLength={maxLength}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
            placeholder={label ? ' ' : placeholder}
            className={`textbox-input ${hasValue ? 'has-value' : ''}`}
          />
          {label && variant === TEXTBOX_VARIANTS.FLOATING && (
            <label htmlFor={name} className="textbox-label">{label}</label>
          )}
        </>
      ) : (
        <>
          <div className="textbox-input-wrapper">
            <input
              id={name}
              name={name}
              type={type}
              value={value}
              onChange={onChange}
              maxLength={maxLength}
              onFocus={() => setIsFocused(true)}
              onBlur={() => setIsFocused(false)}
              placeholder={variant === TEXTBOX_VARIANTS.FLOATING ? ' ' : placeholder}
              className={`textbox-input ${hasValue ? 'has-value' : ''}`}
            />
            {suffix && <span className="textbox-suffix">{suffix}</span>}
          </div>
          {label && variant === TEXTBOX_VARIANTS.FLOATING && (
            <label htmlFor={name} className="textbox-label">{label}</label>
          )}
        </>
      )}
    </div>
  );
};

export default Textbox;
