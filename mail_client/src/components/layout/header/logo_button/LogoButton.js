import './LogoButton.css';
import { useNavigate } from 'react-router-dom';

/**
 * LogoButton – a button styled like Gmail's login logo.
 * Navigates to the inbox when clicked. Uses the image from public/pics.
 */
const LogoButton = () => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/mails/inbox'); 
  };

  return (
    <button className="logo-btn" onClick={handleClick}>
      <img
        src="/pics/mail_icon.png"
        alt="MailMe Logo"
        className="logo-icon"
      />
      <span className="logo-text">MailMe</span>
    </button>
  );
};

export default LogoButton;
