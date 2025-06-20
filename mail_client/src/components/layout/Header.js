import { useState } from 'react';
import GmailButton from './GmailButton';
import SearchTextbox from './SearchTextbox';
import SearchResultsDropdown from './SearchResultsDropdown';
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
  const [searchResults, setSearchResults] = useState([]);
  const [isFocused, setIsFocused] = useState(false);

  // מדמה תוצאות חיפוש מיילים
  const mails = [
    { id: 1, subject: 'מבחן מחשב: ציוני תרגיל בית', from: 'me, inbal0147@icloud.com', date: 'Mar 13' },
    { id: 2, subject: 'תמונה', from: 'עמרי עבדל בן לולו, me', date: '7/27/23' },
    { id: 3, subject: 'הגיב לפוסט זה: זאת תז...', from: 'LinkedIn, me', date: 'Jun 19' },
    { id: 4, subject: 'Fwd: Submission of "ex1"', from: 'me, inbal0147@icloud.com', date: 'Apr 11' },
  ];

  const handleChange = (e) => {
    const value = e.target.value;
    setSearchText(value);

    const filtered = mails.filter(mail =>
      mail.subject.includes(value) || mail.from.includes(value)
    );

    setSearchResults(filtered.slice(0, 5));
  };

  const hasDropdown = isFocused && searchText.length > 0;

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
        <button className="profile-button" onClick={() => console.log('Profile clicked')}>
          <ProfileImage
            src="/logo192.png"
            size="35px"
          />
        </button>
      </div>
    </header>
  );
};

export default Header;

