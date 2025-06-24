import { useState, useEffect, useRef, forwardRef } from 'react';
import './TextBox.css';
import ErrorMessage from '../ErrorMessage';

/**
 * Reusable Textbox component with support for different variants and styles.
 */
const Textbox = forwardRef(({
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
    errorMessage = '',
    autoFocusOnError = false
}, ref) => {
    const [isFocused, setIsFocused] = useState(false);
    const hasValue = !!value && value.trim().length > 0;

    const internalRef = useRef();
    const inputRef = ref || internalRef;

    useEffect(() => {
        if (isInvalid && autoFocusOnError && inputRef.current) {
            inputRef.current.focus();
        }
    }, [isInvalid, autoFocusOnError]);

    const wrapperClass = `
    textbox-wrapper ${size} ${variant}
    ${isFocused ? 'focused' : ''}
    ${hasValue ? 'filled' : ''}
    ${isInvalid ? 'invalid' : ''}
    ${!isInvalid && isValid && hasValue ? 'valid' : ''}
  `.trim();

    return (
        <>
            <div className={wrapperClass}>
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
                        ref={inputRef}
                    />
                    {suffix && <span className="textbox-suffix">{suffix}</span>}
                </div>
                {label && variant === 'floating' && (
                    <label htmlFor={name} className="textbox-label">{label}</label>
                )}
            </div >

            {/* Show error message *below* the wrapper */}
            {
                isInvalid && errorMessage.trim() !== '' && (
                    <ErrorMessage message={errorMessage} />
                )
            }
        </>
    );
});

export default Textbox;
