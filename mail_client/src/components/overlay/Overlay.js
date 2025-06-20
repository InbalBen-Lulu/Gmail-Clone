import './Overlay.css';

/**
 * Overlay component – a semi-transparent layer behind dialogs/modals.
 * Clicking it usually closes the modal (via onClick handler).
 *
 * Props:
 * - onClick: function – triggered when the overlay is clicked
 */
const Overlay = ({ onClick }) => {
  return (
    <div className="overlay" onClick={onClick} aria-hidden="true" />
  );
};

export default Overlay;
