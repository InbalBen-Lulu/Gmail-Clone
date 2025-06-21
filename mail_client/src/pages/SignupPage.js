import SignupForm from '../components/signup/SignupForm';
import ToggleThemeButton from '../components/common/ToggleThemeButton';

/**
 * SignupPage is the main page component for user registration.
 * It renders the SignupForm component inside a basic container.
 */
const SignupPage = () => {
  return (
    <div>
      <div style={{ position: 'relative', minHeight: '100vh' }}>
        <div style={{ position: 'absolute', top: '1rem', right: '1rem' }}>
          <ToggleThemeButton />
        </div>
        <SignupForm />
      </div>
    </div>
  );
};

export default SignupPage;
