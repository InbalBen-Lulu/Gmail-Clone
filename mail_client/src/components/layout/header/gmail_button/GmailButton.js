import './GmailButton.css';
// import { useNavigate } from 'react-router-dom';

/**
 * A button styled like Gmail's login button, with logo and text.
 */
const GmailButton = () => {
  // const navigate = useNavigate();

  const handleClick = () => {
    // Navigate to the inbox page when the button is clicked
    // navigate('/inbox'); 
  };

  return (
    <button className="gmail-btn" onClick={handleClick}>
      <img src="/pics/gmail_logo_icon.ico" alt="Gmail" className="gmail-logo" />
      <span className="gmail-text">Gmail</span>
    </button>
  );
};

export default GmailButton;
