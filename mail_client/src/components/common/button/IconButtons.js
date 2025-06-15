import IconButton from "./IconButton";
import { MdStar, MdStarBorder } from "react-icons/md";
import Icon from "../../../assets/icons/Icon";

/**
 * Main (large) icon button – used in top bar (e.g. settings).
 */
export const MainIconButton = ({ icon, ariaLabel, onClick, className = "" }) => {
  return (
    <IconButton
      icon={<span className="icon">{icon}</span>}
      ariaLabel={ariaLabel}
      onClick={onClick}
      size="large"
      className={className}
    />
  );
};

/**
 * Small icon button – used in lists, inline actions, etc.
 */
export const SmallIconButton = ({ icon, ariaLabel, onClick, className = "" }) => {
  return (
    <IconButton
      icon={icon}
      ariaLabel={ariaLabel}
      onClick={onClick}
      size="small"
      className={className}
    />
  );
};

/**
 * Star toggle button – special styling for starred state.
 */
export const StarButton = ({ isStarred, onClick, className = "" }) => {
  const icon = isStarred ? <MdStar className="star-icon starred" /> : <MdStarBorder className="star-icon" />;

  return (
    <IconButton
      icon={icon}
      ariaLabel={isStarred ? "Unstar" : "Star"}
      onClick={onClick}
      size="small"
      className={`star-button ${className}`.trim()}
    />
  );
};

/**
 * Trash icon button – general small delete button.
 */
export const TrashButton = ({ onClick, className = "" }) => {
  return (
    <SmallIconButton
      icon={<Icon name="delete" />}
      ariaLabel="Delete"
      onClick={onClick}
      className={`trash-button ${className}`.trim()}
    />
  );
};