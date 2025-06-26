import "./MailMeLogo.css";

/**
 * MailMeLogo – shows the MailMe logo with icon and text.
 * Used in signin/signup.
 */
const MailMeLogo = () => {
  return (
    <div className="mailme-logo">
      <img
        src="/pics/mail_icon.png"
        alt="MailMe icon"
        className="mailme-logo-icon"
      />
      <span className="mailme-logo-text">MailMe</span>
    </div>
  );
};

export default MailMeLogo;
