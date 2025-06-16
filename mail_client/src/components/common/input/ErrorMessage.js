import { MdError } from 'react-icons/md';
import './ErrorMessage.css';

/**
 * A reusable component to display an error message with an icon.
 * Renders nothing if no message is provided.
 */
const ErrorMessage = ({ message }) => {
  if (!message) return null;

  return (
    <div className="error-container">
      <span className="error-icon"><MdError /></span>
      <span className="error-message">{message}</span>
    </div>
  );
};

export default ErrorMessage;
