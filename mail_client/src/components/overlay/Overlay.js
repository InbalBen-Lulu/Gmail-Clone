import './Overlay.css';

const Overlay = ({ onClick }) => {
  return (
    <div className="overlay" onClick={onClick} aria-hidden="true" />
  );
};

export default Overlay;
