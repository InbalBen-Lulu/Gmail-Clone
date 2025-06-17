import { useRef, forwardRef, useState } from 'react';
import './SearchTextBox.css';

/**
 * A reusable search input textbox with optional ref forwarding.
 * Includes internal focus handling for visual styling.
 */
const SearchTextbox = forwardRef(({ value, onChange, placeholder = 'Search' }, refFromParent) => {
  const internalRef = useRef(null);
  const inputRef = refFromParent || internalRef;

  const [isFocused, setIsFocused] = useState(false);

  return (
    <div
      className={`textbox-wrapper search ${isFocused ? 'focused' : ''}`}
      onClick={() => inputRef.current?.focus()}
    >
      <input
        type="text"
        className="textbox-input"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        ref={inputRef}
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
      />
    </div>
  );
});


export default SearchTextbox;