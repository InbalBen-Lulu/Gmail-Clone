import { useState } from 'react';
import Textbox from '../common/input/TextBox';
import TextButton from '../common/button/TextButton';
import Checkbox from '../common/Checkbox';
import useAuthFetch from '../../hooks/useAuthFetch';
import "./SigninForm.css";

const SigninForm = () => {
    const [step, setStep] = useState(1);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    // const [emailTouched, setEmailTouched] = useState(false);

    const fetchWithAuth = useAuthFetch();

    // const handleEmailSubmit = async () => {
    //     try {
    //         const res = await fetchWithAuth(`/api/users/${email.toLowerCase()}/public`);
    //         if (res.status === 200) {
    //             setStep(2);
    //             setEmailError('');
    //         } else {
    //             setEmailError('User not found');
    //         }
    //     } catch (err) {
    //         setEmailError('Something went wrong');
    //     }
    // };

    const handleEmailSubmit = async () => {
    if (email.toLowerCase() !== 'moria') {
        setEmailError('Only "moria" is allowed for testing');
        return;
    }

    // simulate success
    setStep(2);
    setEmailError('');
    };

    const handlePasswordSubmit = async () => {
        try {
            const res = await fetchWithAuth(`/api/tokens/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: email.toLowerCase(), password })
            });
            if (res.status === 200) {
                setPasswordError('');
                // redirect to dashboard or inbox
            } else {
                const data = await res.json();
                setPasswordError(data.error || 'Invalid password');
            }
        } catch (err) {
            setPasswordError('Something went wrong');
        }
    };

    return (
        <div className="signin-wrapper">
            <div className="signin-container">
                {/* Left section */}
                <div className="signin-left">
                    <img src="/pics/google-g-icon.png" alt="Google logo" className="google-logo" />
                    <h2 className="signin-title">
                        {step === 1 ? 'Sign in' : `Hi ${email.split('@')[0]}`}
                    </h2>
                    {step === 1 && (
                        <p className="signin-subtext">
                            with your Google Account to continue to Gmail.
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
                                <TextButton variant="ghost" onClick={() => alert("Redirect to signup")}>
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
                                <TextButton variant="primary" onClick={handlePasswordSubmit}>
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
