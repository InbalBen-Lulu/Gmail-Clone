import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import SignupPage from './pages/SignupPage';
import SigninPage from './pages/SigninPage';
import MailPage from './pages/MailPage';
import PrivateRoute from './components/common/PrivateRoute';

import { AuthProvider } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
  return (
    // Wrap entire app with authentication and theme context providers
    <AuthProvider>
      <ThemeProvider>
          <Router>
            <Routes>
              {/* Public routes for authentication */}
              <Route path="/signin" element={<SigninPage />} />
              <Route path="/signup" element={<SignupPage />} />

              {/* Protected route for mail category view */}
              <Route path="/mails/:category" element={
                <PrivateRoute>
                  <MailPage />
                </PrivateRoute>
              } />

              {/* Protected route for specific mail view */}
              <Route path="/mails/:category/:mailId" element={
                <PrivateRoute>
                  <MailPage />
                </PrivateRoute>
              } />

              {/* Protected route for personal info view */}
              <Route path="/personal-info" element={
                <PrivateRoute>
                  <PersonalInfoPage />
                </PrivateRoute>
              } />

              {/* Redirect root path to signin */}
              <Route path="/" element={<Navigate to="/signin" replace />} />

              {/* Catch-all route redirects unknown paths to signin */}
              <Route path="*" element={<Navigate to="/signin" replace />} />
            </Routes>
          </Router>
      </ThemeProvider>
    </AuthProvider>
  );
}

export default App;
