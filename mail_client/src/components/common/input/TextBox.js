import { useState } from 'react';
import './TextBox.css';
import { MdError } from 'react-icons/md';

/**
 * Reusable Textbox component with support for different variants and styles.
 */
const Textbox = ({
  label,
  name,
  value,
  onChange,
  maxLength,
  placeholder = '',
  type = 'text',
  size = 'md',
  variant = 'floating',
  suffix = '',
  isInvalid = false,
  isValid = false,
  errorMessage = ''
}) => {
  const [isFocused, setIsFocused] = useState(false);
  const hasValue = value?.length > 0;
  const isTextarea = variant === 'compose body';

  const wrapperClass = `
    textbox-wrapper ${size} ${variant}
    ${isFocused ? 'focused' : ''}
    ${hasValue ? 'filled' : ''}
    ${isInvalid ? 'invalid' : ''}
    ${!isInvalid && isValid ? 'valid' : ''}
  `.trim();

  return (
    <>
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
            {label && variant === 'floating' && (
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
                placeholder={variant === 'floating' ? ' ' : placeholder}
                className={`textbox-input ${hasValue ? 'has-value' : ''}`}
              />
              {suffix && <span className="textbox-suffix">{suffix}</span>}
            </div>
            {label && variant === 'floating' && (
              <label htmlFor={name} className="textbox-label">{label}</label>
            )}
          </>
        )}
      </div>

      {/* Show error message *below* the wrapper */}
      {/* {isInvalid && errorMessage && (
        <div className="error-message">{errorMessage}</div>
      )} */}
      {isInvalid && errorMessage && (
        <div className="error-container">
          <span className="error-icon">< MdError /></span>
          <span className="error-message">{errorMessage}</span>
        </div>
      )}
    </>
  );
};

export default Textbox;
