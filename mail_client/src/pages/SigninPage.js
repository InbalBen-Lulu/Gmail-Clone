import SigninForm from '../components/signin/SigninForm';
import ToggleThemeButton from '../components/common/ToggleThemeButton';

/**
 * SigninPage is the main page component for handling user sign-in.
 * It renders the SigninForm inside a simple container.
 */
const SigninPage = () => {
  return (
    <div>
      <div style={{ position: 'relative', minHeight: '100vh' }}>
        <div style={{ position: 'absolute', top: '1rem', right: '1rem' }}>
          <ToggleThemeButton />
        </div>
        <SigninForm />
      </div>
    </div>
  );
};

export default SigninPage;
