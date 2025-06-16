import { useState } from 'react';
import Step1Name from './steps/Step1Name';
import Step2Birth from './steps/Step2Birth';
import Step3Username from './steps/Step3Username';
import Step4Password from './steps/Step4Password';
import { months, genders } from './steps/constants';
import './SignupForm.css';

const SignupForm = () => {
    const [step, setStep] = useState(1);

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [month, setMonth] = useState('');
    const [day, setDay] = useState('');
    const [year, setYear] = useState('');
    const [gender, setGender] = useState('');
    const [gmail, setGmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirm, setConfirm] = useState('');
    const [showPassword, setShowPassword] = useState(false);

    const [firstNameError, setFirstNameError] = useState('');
    const [monthError, setMonthError] = useState('');
    const [dayError, setDayError] = useState('');
    const [yearError, setYearError] = useState('');
    const [genderError, setGenderError] = useState('');
    const [gmailError, setGmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [confirmError, setConfirmError] = useState('');

    return (
        <div className="signup-wrapper">
            <div className="signup-container">
                <div className="signup-left">
                    <img src="/pics/google-g-icon.png" alt="Google logo" className="google-logo" />
                    <h2 className="signup-title">
                        {step === 1 && 'Create a Google Account'}
                        {step === 2 && 'Basic information'}
                        {step === 3 && 'How you’ll sign in'}
                        {step === 4 && 'Create a strong password'}
                    </h2>
                    <p className="signup-subtext">
                        {step === 1 && 'Enter your name'}
                        {step === 2 && 'Enter your birthday and gender'}
                        {step === 3 && 'Create a Gmail address for signing in to your Google Account'}
                        {step === 4 && 'Create a strong password with a mix of letters, numbers and symbols'}
                    </p>
                </div>

                <div className="signup-box">
                    {step === 1 && (
                        <Step1Name
                            firstName={firstName}
                            setFirstName={setFirstName}
                            lastName={lastName}
                            setLastName={setLastName}
                            firstNameError={firstNameError}
                            setFirstNameError={setFirstNameError}
                            onNext={() => setStep(2)}
                        />
                    )}

                    {step === 2 && (
                        <Step2Birth
                            month={month}
                            day={day}
                            year={year}
                            gender={gender}
                            setMonth={setMonth}
                            setDay={setDay}
                            setYear={setYear}
                            setGender={setGender}
                            dayError={dayError}
                            monthError={monthError}
                            yearError={yearError}
                            genderError={genderError}
                            setDayError={setDayError}
                            setMonthError={setMonthError}
                            setYearError={setYearError}
                            setGenderError={setGenderError}
                            onNext={() => setStep(3)}
                            months={months}
                            genders={genders}
                        />
                    )}

                    {step === 3 && (
                        <Step3Username
                            gmail={gmail}
                            setGmail={setGmail}
                            gmailError={gmailError}
                            setGmailError={setGmailError}
                            onNext={() => setStep(4)}
                        />
                    )}

                    {step === 4 && (
                        <Step4Password
                            password={password}
                            confirm={confirm}
                            showPassword={showPassword}
                            setPassword={setPassword}
                            setConfirm={setConfirm}
                            setShowPassword={setShowPassword}
                            passwordError={passwordError}
                            confirmError={confirmError}
                            setPasswordError={setPasswordError}
                            setConfirmError={setConfirmError}
                            onNext={() => alert('Submit form')}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default SignupForm;
