import { useState } from 'react';
import Textbox from '../common/input/textBox/TextBox';
import TextButton from '../common/button/TextButton';
import Checkbox from '../common/check_box/Checkbox';
import { useUserService } from '../../services/useUserService';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import MailMeLogo from '../common/logo/MailMeLogo';
import "./SigninForm.css";

/**
 * SigninForm component handles a two-step Gmail-style sign-in:
 * Step 1 – user enters email
 * Step 2 – user enters password
 * Shows validation errors and conditionally renders based on step
 */
const SigninForm = () => {
    const [step, setStep] = useState(1);
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');

    const navigate = useNavigate();

    // Redirect to the signup page
    const handleSignupRedirect = () => {
        navigate('/signup');
    };

    const { fetchPublicUser } = useUserService();

    // Submit the email and fetch public user info (e.g. name)
    const handleEmailSubmit = async () => {
        try {
            const { status, data } = await fetchPublicUser(email);
            if (status === 200) {
                setName(data.name);
                setStep(2);
                setEmailError('');
            } else {
                setEmailError(data?.error || 'User not found');
            }
        } catch (err) {
            setEmailError('Something went wrong');
        }
    };

    const { login } = useAuth();

    // Submit login credentials and authenticate user
    const handleLogin = async () => {
        try {
            setPasswordError('');
            const user = await login(email, password);
            if (user) {
                navigate('/mails/inbox');
            }
        } catch (err) {
            setPasswordError(err.message || 'Login failed');
        }
    };

    return (
        <div className="signin-wrapper">
            <div className="signin-container">
                {/* Left section */}
                <div className="signin-left">
                    <MailMeLogo />
                    <h2 className="signin-title">
                        {step === 1 ? 'Sign in' : `Hi ${name}`}
                    </h2>
                    {step === 1 && (
                        <p className="signin-subtext">
                            with your Google Account to continue to MailMe.
                        </p>
                    )}
                </div>
                {/* Right section */}
                <div className="signin-box">
                    {step === 1 && (
                        <>
                            <div className="form-group">
                                <Textbox
                                    autoFocus
                                    label="Email"
                                    name="Email"
                                    value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value);
                                    }}
                                    variant={'floating'}
                                    isInvalid={!!emailError}
                                    isValid={!emailError}
                                    errorMessage={emailError}
                                />
                            </div>

                            <p className="guest-hint">
                                Not your computer? Use Guest mode to sign in privately.
                            </p>

                            <div className="button-row">
                                <TextButton variant="ghost" onClick={handleSignupRedirect}>
                                    Create account
                                </TextButton>
                                <TextButton variant="primary" onClick={handleEmailSubmit}>
                                    Next
                                </TextButton>
                            </div>
                        </>
                    )}

                    {step === 2 && (
                        <>
                            <div className="form-group position-relative">
                                <Textbox
                                    autoFocus
                                    label="Enter your password"
                                    name="password"
                                    type={showPassword ? 'text' : 'password'}
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value);
                                        if (passwordError) setPasswordError('');
                                    }}
                                    variant='floating'
                                    isInvalid={!!passwordError}
                                    isValid={!passwordError}
                                    errorMessage={passwordError}
                                />
                            </div>

                            <div className="form-group">
                                <label style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                                    <Checkbox checked={showPassword} onChange={() => setShowPassword((prev) => !prev)} />
                                    Show password
                                </label>
                            </div>

                            <div className="button-row">
                                <TextButton variant="primary" onClick={handleLogin}>
                                    Next
                                </TextButton>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SigninForm;
