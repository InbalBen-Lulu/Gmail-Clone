import { useState, useEffect, useRef } from 'react';
import GmailButton from './gmail_button/GmailButton';
import SearchTextbox from './search_bar/SearchTextbox';
import SearchResultsDropdown from './search_bar/SearchResultsDropdown';
import { MainIconButton } from '../../common/button/IconButtons';
import Icon from '../../../assets/icons/Icon';
import ToggleThemeButton from '../../common/ToggleThemeButton';
import ProfileImage from '../../common/profile_image/ProfileImage';
import useMailService from '../../../services/useMailService';
import ProfileMenu from './profile_menu/ProfileMenu';
import './Header.css';

/**
 * Header component – Gmail-style top bar with menu icon, logo and search box.
 */
const Header = () => {
  const [searchText, setSearchText] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isFocused, setIsFocused] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const profileMenuRef = useRef(null);

  const searchMails = useMailService();

  const handleChange = async (e) => {
    const value = e.target.value;
    setSearchText(value);

    const results = await searchMails(value);
    setSearchResults(results.slice(0, 5));
  };

  const hasDropdown = isFocused && searchText.length > 0;

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (
        profileMenuRef.current &&
        !profileMenuRef.current.contains(e.target)
      ) {
        setIsProfileOpen(false);
      }
    };

    if (isProfileOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isProfileOpen]);

  return (
    <>
      <header className="app-header">
        {/* Left: menu + Gmail logo */}
        <div className="header-left">
          <MainIconButton
            icon={<Icon name="menu" />}
            ariaLabel="Open menu"
            className="menu-button"
            onClick={() => console.log('Menu clicked')}
          />
          <GmailButton />
        </div>

        {/* Center: search + dropdown */}
        <div className="header-center">
          <div className="search-wrapper">
            <SearchTextbox
              value={searchText}
              onChange={handleChange}
              onFocus={() => setIsFocused(true)}
              onBlur={() => setTimeout(() => setIsFocused(false), 150)}
              hasDropdown={hasDropdown}
            />
            <SearchResultsDropdown
              results={searchResults}
              visible={hasDropdown}
              searchText={searchText}
            />
          </div>
        </div>

        {/* Right: theme toggle + profile image */}
        <div className="header-right">
          <ToggleThemeButton />
          <button
            className="profile-button"
            onClick={() => setIsProfileOpen(prev => !prev)}
          >
            <ProfileImage
              src="/logo192.png"
              size="35px"
            />
          </button>
        </div>
      </header>

      {/* Popup Profile Menu – anchored to top right */}
      {isProfileOpen && (
        <div className="profile-menu-popup" ref={profileMenuRef}>
          <ProfileMenu onClose={() => setIsProfileOpen(false)} />
        </div>
      )}
    </>
  );
};

export default Header;
