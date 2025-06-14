import { useTheme } from "../../contexts/ThemeContext";
import "./ToggleThemeButton.css";

/**
 * A toggle switch component to switch between light and dark mode.
 * It uses the useTheme hook to access and toggle the current theme.
 */
const ToggleThemeButton = () => {
  const { darkMode, toggleTheme } = useTheme(); // Get current mode and toggle function

  return (
    <label className="theme-switch">
      {/* Hidden checkbox controlling the toggle state */}
      <input
        type="checkbox"
        checked={darkMode}
        onChange={toggleTheme}
      />
      {/* Custom styled slider (controlled via CSS) */}
      <span className="slider" />
    </label>
  );
};

export default ToggleThemeButton;
