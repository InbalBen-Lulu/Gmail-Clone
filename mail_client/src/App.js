// import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
// import SignupPage from './pages/SignupPage';
// import SigninPage from './pages/SigninPage';

// import { AuthProvider } from './contexts/AuthContext';
// import { ThemeProvider } from './contexts/ThemeContext';


// function App() {
//   return (
//     <AuthProvider>
//       <ThemeProvider>
//         <Router>
//           <Routes>
//             <Route path="/signin" element={<SigninPage />} />
//             <Route path="/signup" element={<SignupPage />} />
//             <Route path="*" element={<Navigate to="/signin" replace />} />
//           </Routes>
//         </Router>
//       </ThemeProvider>
//     </AuthProvider>
//   );
// }

// export default App;
import Header from './components/layout/Header';
import { ThemeProvider } from './contexts/ThemeContext';

/**
 * App component – renders the demo page.
 */
function App() {
  return (
    <ThemeProvider>
      <Header />
    </ThemeProvider>
  );
}

export default App;