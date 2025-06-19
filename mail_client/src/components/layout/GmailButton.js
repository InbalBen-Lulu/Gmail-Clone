import './GmailButton.css';

/**
 * A button styled like Gmail's login button, with logo and text.
 */
const GmailButton = ({ onClick }) => {
  return (
    <button className="gmail-btn" onClick={onClick}>
      <img src="/pics/gmail_logo_icon.ico" alt="Gmail" className="gmail-logo" />
      <span className="gmail-text">Gmail</span>
    </button>
  );
};

export default GmailButton;
