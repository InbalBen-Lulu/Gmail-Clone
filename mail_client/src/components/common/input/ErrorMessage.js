import { MdError } from 'react-icons/md';
import './ErrorMessage.css';

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
