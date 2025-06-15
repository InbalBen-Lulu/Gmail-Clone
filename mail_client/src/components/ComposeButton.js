import Icon from "../assets/icons/Icon";
import "./ComposeButton.css";

/**
 * ComposeButton – standalone button used for composing a new message.
 * Styled with a unique background and rounded edges.
 * 
 * Props:
 * - onClick: function to handle button click
 * - className: optional additional CSS classes
 */
const ComposeButton = ({ onClick, className = "" }) => {
    // Combine base and optional class names
    const finalClass = `btn compose ${className}`.trim();

    return (
      <button onClick={onClick} className={finalClass}>
        <span className="btn-icon">
          <Icon name="edit" />
        </span>
       Compose
      </button>
    );
};

export default ComposeButton;
