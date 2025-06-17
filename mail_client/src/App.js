import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import DemoForm from './pages/DemoForm';
import DemoSignup from './pages/SignupPage';
import DemoSignin from './pages/SigninPage';

import { AuthProvider } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';


function App() {
  return (
    <AuthProvider>
      <ThemeProvider>
        <Router>
          <Routes>
            <Route path="/signin" element={<DemoSignin />} />
            <Route path="/signup" element={<DemoSignup />} />
            <Route path="*" element={<Navigate to="/signin" replace />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </AuthProvider>
  );
}

export default App;