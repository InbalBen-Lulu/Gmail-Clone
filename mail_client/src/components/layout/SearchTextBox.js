import { useRef, forwardRef, useState } from 'react';
import { MainIconButton } from '../common/button/IconButtons';
import Icon from '../../assets/icons/Icon';
import './SearchTextbox.css';

/**
 * A reusable search input textbox with optional ref forwarding.
 * Includes internal focus handling for visual styling.
 */
const SearchTextbox = forwardRef(({ value, onChange }, refFromParent) => {
  const internalRef = useRef(null);
  const inputRef = refFromParent || internalRef;

  const [isFocused, setIsFocused] = useState(false);

  return (
    <div
      className={`textbox-wrapper search search-context ${isFocused ? 'focused' : ''}`}
      onClick={() => inputRef.current?.focus()}
    >
      {/* Search Icon button on the left */}
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
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
      />
    </div>
  );
});

export default SearchTextbox;
