import { useState, useRef, forwardRef } from 'react';
import './ComposeTextBox.css';

/**
 * ComposeTextbox is a reusable input component used in email compose interfaces.
 * It supports both single-line input ('compose') and multi-line textarea ('compose body').
 * Applies 'focused' and 'filled' classes based on user interaction and value state.
 */
const ComposeTextbox = forwardRef(({
  name,
  value,
  onChange,
  placeholder = '',
  variant = 'compose',
}, ref) => {
  const [isFocused, setIsFocused] = useState(false);
  const hasValue = !!value && value.trim().length > 0;
  const isTextarea = variant === 'compose body';
  const internalRef = useRef();
  const inputRef = ref || internalRef;

  const wrapperClass = `
    textbox-wrapper ${variant}
    ${isFocused ? 'focused' : ''}
    ${hasValue ? 'filled' : ''}
  `.trim();

  return (
    <div className={wrapperClass}>
      {isTextarea ? (
        <textarea
          id={name}
          name={name}
          value={value}
          onChange={onChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="textbox-input"
          ref={inputRef}
        />
      ) : (
        <input
          id={name}
          name={name}
          type="text"
          value={value}
          onChange={onChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="textbox-input"
          ref={inputRef}
        />
      )}
    </div>
  );
});

export default ComposeTextbox;
