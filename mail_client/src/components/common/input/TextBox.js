import React, { useState } from 'react';
import './TextBox.css';
import { TEXTBOX_VARIANTS } from './constants';

const Textbox = ({
  label,
  name,
  value,
  onChange,
  placeholder = '',
  type = 'text',
  size = 'md',
  variant = TEXTBOX_VARIANTS.FLOATING,
  suffix = ''
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
