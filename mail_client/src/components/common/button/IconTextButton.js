import Icon from "../../../assets/icons/Icon";
import "./IconTextButton.css";

/**
 * General-purpose Icon + Text button.
 * Props:
 * - iconName: string – name of icon to display (from Icon component)
 * - text: string – button label
 * - onClick: function – click handler
 * - className: string – additional class for styling variant
 * - style: object – optional inline styles (e.g., width, margin)
 */
const IconTextButton = ({ iconName, text, onClick, className = "", style = {} }) => {
  return (
    <button
      className={`btn icon-text ${className}`.trim()}
      onClick={onClick}
      style={style}
    >
      <span className="btn-icon">
        <Icon name={iconName} />
      </span>
      {text}
    </button>
  );
};

export default IconTextButton;

/**
 * ComposeButton – preset variant for creating new items (e.g., emails).
 * Icon: edit
 * Text: "Compose"
 */
export const ComposeButton = (props) => (
  <IconTextButton
    iconName="edit"
    text="Compose"
    className="compose"
    {...props}
  />
);

/**
 * ProfileActionButton – reusable button for profile actions.
 * Can be used for "Add", "Change", or "Remove" actions on a profile picture.
 * Props:
 * - iconName: string – icon to show (e.g. "edit", "add_a_photo", "delete")
 * - text: string – button label (e.g. "Change")
 * - onClick: function – handler for click
 * - width: string – optional CSS width value (default: "100%")
 */
export const ProfileActionButton = ({
  iconName,
  text,
  onClick,
  width = "100%",
}) => {
  return (
    <IconTextButton
      iconName={iconName}
      text={text}
      onClick={onClick}
      className="profile-action"
      style={{ width }}
    />
  );
};
