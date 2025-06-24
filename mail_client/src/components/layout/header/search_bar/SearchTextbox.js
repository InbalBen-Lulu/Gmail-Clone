import { useRef, forwardRef, useState } from 'react';
import { MainIconButton } from '../../../common/button/IconButtons';
import Icon from '../../../../assets/icons/Icon';
import './SearchTextbox.css';

/**
 * A reusable search input textbox with optional ref forwarding.
 * Includes internal focus handling and external styling for dropdown.
 *
 * Props:
 * - value: string
 * - onChange: function
 * - onFocus / onBlur: optional event handlers
 * - hasDropdown: boolean – whether dropdown is open (for style)
 */
const SearchTextbox = forwardRef(({ value, onChange, onFocus, onBlur, hasDropdown }, refFromParent) => {
  const internalRef = useRef(null);
  const inputRef = refFromParent || internalRef;

  const [isFocused, setIsFocused] = useState(false);

  /**
   * Handles input focus event, sets internal focus state and calls external onFocus if provided.
   */
  const handleFocus = (e) => {
    setIsFocused(true);
    if (onFocus) onFocus(e);
  };

  /**
   * Handles input blur event, resets internal focus state and calls external onBlur if provided.
   */
  const handleBlur = (e) => {
    setIsFocused(false);
    if (onBlur) onBlur(e);
  };

  return (
    <div
      className={`textbox-wrapper search search-context 
        ${isFocused ? 'focused' : ''} 
        ${hasDropdown ? 'with-dropdown' : ''}`}
      onClick={() => inputRef.current?.focus()}
    >
      <div className="search-icon">
        <MainIconButton
          icon={<Icon name="search" />}
          ariaLabel="Search"
          className="search-button"
          onClick={() => inputRef.current?.focus()}
        />
      </div>

      <input
        type="text"
        className="textbox-input"
        value={value ?? ''}
        onChange={onChange}
        placeholder="Search mail"
        ref={inputRef}
        onFocus={handleFocus}
        onBlur={handleBlur}
      />

      {value && (
        <div className="clear-icon">
          <MainIconButton
            icon={<Icon name="close" />}
            ariaLabel="Clear search"
            className="clear-button"
            onClick={() => onChange({ target: { value: '' } })}
          />
        </div>
      )}
    </div>
  );
});

export default SearchTextbox;
