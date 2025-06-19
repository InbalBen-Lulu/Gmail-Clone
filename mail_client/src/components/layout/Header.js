import { useState } from 'react';
import GmailButton from './GmailButton';
import SearchTextbox from './SearchTextbox';
import { MainIconButton } from '../common/button/IconButtons';
import Icon from '../../assets/icons/Icon';
import ToggleThemeButton from '../common/ToggleThemeButton';
import ProfileImage from '../common/profile_image/ProfileImage';
import './Header.css';

/**
 * Header component – Gmail-style top bar with menu icon, logo and search box.
 */
const Header = () => {
    const [searchText, setSearchText] = useState('');

  return (
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

      {/* Center: search */}
      <div className="header-center">
        <SearchTextbox
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
      </div>

      {/* Right: theme toggle + profile image */}
      <div className="header-right">
        <ToggleThemeButton />
        <button className="profile-button" onClick={() => console.log('Profile clicked')}>
          <ProfileImage
            src="/logo192.png"  // ניתן להחליף לתמונה דינמית
            size="35px"
          />
        </button>
      </div>
    </header>
  );
};

export default Header;
