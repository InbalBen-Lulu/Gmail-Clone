import React, { createContext, useContext, useState, useEffect } from "react";

// Create a new ThemeContext
const ThemeContext = createContext();

// The provider component that wraps the app and provides theme state
export const ThemeProvider = ({ children }) => {
  const [darkMode, setDarkMode] = useState(false); // false = light mode by default

  // When darkMode changes, update the <body> class accordingly
  useEffect(() => {
    document.body.className = darkMode ? "dark-mode" : "light-mode";
  }, [darkMode]);

  // Function to toggle between light and dark mode
  const toggleTheme = () => setDarkMode((prev) => !prev);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

// Custom hook for consuming the theme context
export const useTheme = () => useContext(ThemeContext);
