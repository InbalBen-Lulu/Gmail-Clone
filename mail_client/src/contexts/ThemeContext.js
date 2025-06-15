import { createContext, useContext, useState, useEffect } from "react";

// Context for managing theme (light/dark)
const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
  // Check if user has a saved theme in localStorage, fallback to false (light mode)
  const [darkMode, setDarkMode] = useState(() => {
    const stored = localStorage.getItem("darkMode");
    return stored === "true";
  });

  // Apply theme class and save preference to localStorage
  useEffect(() => {
    document.body.classList.remove("light-mode", "dark-mode");
    document.body.classList.add(darkMode ? "dark-mode" : "light-mode");

    localStorage.setItem("darkMode", darkMode); // Save preference
  }, [darkMode]);

  // Toggle between light and dark mode
  const toggleTheme = () => setDarkMode((prev) => !prev);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

// Custom hook for accessing theme context
export const useTheme = () => useContext(ThemeContext);
export default ThemeProvider;
