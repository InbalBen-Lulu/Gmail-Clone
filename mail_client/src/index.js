import ReactDOM from 'react-dom/client';
import { ThemeProvider } from './contexts/ThemeContext';
import './index.css';
import App from './App';
import './styles/colors.css';
import './styles/theme.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ThemeProvider>
    <App />
  </ThemeProvider>
);
