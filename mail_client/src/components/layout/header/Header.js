import { useState, useEffect, useRef } from 'react';
import LogoButton from './logo_button/LogoButton';
import SearchTextbox from './search_bar/SearchTextbox';
import SearchResultsDropdown from './search_bar/SearchResultsDropdown';
import { MainIconButton } from '../../common/button/IconButtons';
import Icon from '../../../assets/icons/Icon';
import ToggleThemeButton from '../../common/ToggleThemeButton';
import ProfileImage from '../../common/profile_image/ProfileImage';
import ProfileMenu from './profile_menu/ProfileMenu';
import { useMailService } from '../../../services/useMailService';
import { useAuth } from '../../../contexts/AuthContext';
import './Header.css';

/**
 * Header component – Gmail-style top bar with optional menu button, search bar and theme toggle.
 *
 * Props:
 * - onToggleSidebar: function – called when menu button is clicked
 * - showSearch: boolean – whether to show the search bar (default: true)
 * - showMenuButton: boolean – whether to show the left menu button (default: true)
 * - background: string – optional background color (e.g. "white", "#f2f2f2")
 */
const Header = ({
  onToggleSidebar,
  showSearch = true,
  showMenuButton = true,
  background
}) => {
  const { user } = useAuth();
  const [searchText, setSearchText] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isFocused, setIsFocused] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileMenuRef = useRef(null);
  const { fetchMails } = useMailService();

  /**
   * Handles input change in the search bar.
   * Updates search text and retrieves top 5 matching mail results.
   * 
   * @param {object} e - Input change event
   */
  const handleChange = async (e) => {
    const value = e.target.value;
    setSearchText(value);

    if (!value) {
      setSearchResults([]);
      return;
    }

    try {
      const { mails } = await fetchMails(0, 5, `search-${value}`);
      setSearchResults(mails);
    } catch (err) {
      console.error('Failed to fetch search results:', err.message);
      setSearchResults([]);
    }
  };

  // Determines if the search dropdown should be visible
  const hasDropdown = isFocused && searchText.length > 0;

  useEffect(() => {
    // Closes the profile menu if user clicks outside of it
    const handleClickOutside = (e) => {
      if (profileMenuRef.current && !profileMenuRef.current.contains(e.target)) {
        setIsProfileOpen(false);
      }
    };

    if (isProfileOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isProfileOpen]);

  return (
    <>
      <header
        className="app-header"
        style={background ? { backgroundColor: background } : {}}
      >
        {/* Left: menu + logo */}
        <div className="header-left">
          {showMenuButton && (
            <MainIconButton
              icon={<Icon name="menu" />}
              ariaLabel="Toggle menu"
              className="menu-button"
              onClick={onToggleSidebar}
            />
          )}
          <LogoButton />
        </div>

        {/* Center: search box */}
        {showSearch && (
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
        )}

        {/* Right: theme toggle + profile image */}
        <div className="header-right">
          <ToggleThemeButton />
          {user && (
            <button
              className="profile-button"
              onClick={() => setIsProfileOpen(prev => !prev)}
            >
              <ProfileImage
                src={user.profileImage}
                size="35px"
              />
            </button>
          )}
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

