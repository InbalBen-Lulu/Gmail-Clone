import Textbox from '../../common/input/TextBox';
import Checkbox from '../../common/Checkbox';
import TextButton from '../../common/button/TextButton';
import './Step4Password.css';
import ErrorMessage from '../../common/input/ErrorMessage';
import { useState } from 'react';

const Step4Password = ({
    password,
    confirm,
    showPassword,
    setPassword,
    setConfirm,
    setShowPassword,
    passwordError,
    confirmError,
    onNext
}) => {
    const [changePasswordError, setChangePasswordError] = useState(''); 
    const handleNext = () => {
        let hasError = false;

        if (password.length < 8) {
            setChangePasswordError('Use 8 characters or more for your password');
            hasError = true;
        } 
        else if (confirm !== password) {
            setChangePasswordError("Those passwords didn’t match. Try again.");
            hasError = true;
        }
        else {
            setChangePasswordError('');
        }

        if (!hasError) onNext();
    };


    return (
        <>
            <Textbox
                label="Password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                variant='floating'
                isInvalid={!!passwordError}
                isValid={!passwordError}
                errorMessage={passwordError}
                autoFocusOnError
            />
            <Textbox
                label="Confirm"
                name="confirm"
                type={showPassword ? 'text' : 'password'}
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                variant='floating'
                isInvalid={!!confirmError}
                isValid={!confirmError}
                errorMessage={confirmError}
                autoFocusOnError
            />
             {changePasswordError && (
                <ErrorMessage message={changePasswordError} />

            )}
            <div className="show-password-row">
                <Checkbox checked={showPassword} onChange={() => setShowPassword((prev) => !prev)} />
                <label>Show password</label>
            </div>
            <div className="button-row">
                <TextButton variant="primary" onClick={handleNext}>Next</TextButton>
            </div>
        </>
    );
};

export default Step4Password;
