import React, { createContext, useContext, useState, useEffect } from "react";

// Context for managing theme (light/dark)
const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
  // Track current theme state
  const [darkMode, setDarkMode] = useState(false);

  // Update <body> class when theme changes
  useEffect(() => {
    document.body.className = darkMode ? "dark-mode" : "light-mode";
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
