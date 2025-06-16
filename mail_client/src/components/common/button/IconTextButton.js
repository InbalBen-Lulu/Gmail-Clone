import Icon from "../../../assets/icons/Icon";
import "./IconTextButton.css";

/**
 * General-purpose Icon + Text button.
 * Props:
 * - iconName: string – name of icon to display (from Icon component)
 * - text: string – button label
 * - onClick: function – click handler
 * - className: string – additional class for styling variant
 */
const IconTextButton = ({ iconName, text, onClick, className = "" }) => {
  return (
    <button className={`btn icon-text ${className}`.trim()} onClick={onClick}>
      <span className="btn-icon">
        <Icon name={iconName} />
      </span>
      {text}
    </button>
  );
};

export default IconTextButton;

/**
 * ComposeButton – specific variant for composing a new item (e.g., email).
 * Icon: edit, Label: "Compose"
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
 * AddProfileButton – specific variant for adding a profile photo.
 * Icon: add_a_photo, Label: "Add profile picture"
 */
export const AddProfileButton = (props) => (
  <IconTextButton
    iconName="add_a_photo"
    text="Add profile picture"
    className="add-profile"
    {...props}
  />
);
