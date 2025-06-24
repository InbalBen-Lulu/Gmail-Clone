import { useState } from 'react';
import Textbox from '../../common/input/textBox/TextBox';
import TextButton from '../../common/button/TextButton';

/**
 * Step 1: Collects user's first and last name.
 * Validates that both first and last names are not empty before proceeding.
 */
const Step1Name = ({
    firstName,
    setFirstName,
    lastName,
    setLastName,
    firstNameError,
    setFirstNameError,
    lastNameError,
    setLastNameError,
    onNext
}) => {
    const [firstNameTouched, setFirstNameTouched] = useState(false);
    const [lastNameTouched, setLastNameTouched] = useState(false);

    const handleNext = () => {
        setFirstNameTouched(true);
        setLastNameTouched(true);

        let hasError = false;

        if (!firstName.trim()) {
            setFirstNameError('First name is required');
            hasError = true;
        } else {
            setFirstNameError('');
        }

        if (!lastName.trim()) {
            setLastNameError('Last name is required');
            hasError = true;
        } else {
            setLastNameError('');
        }

        if (!hasError) {
            onNext();
        }
    };

    return (
        <>
            <Textbox
                label="First name"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                onBlur={() => setFirstNameTouched(true)}
                variant='floating'
                isInvalid={!!firstNameError}
                isValid={firstNameTouched && !!firstName.trim() && !firstNameError}
                errorMessage={firstNameError}
                autoFocusOnError
            />
            <Textbox
                label="Last name"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                onBlur={() => setLastNameTouched(true)}
                variant='floating'
                isInvalid={!!lastNameError}
                isValid={lastNameTouched && !!lastName.trim() && !lastNameError}
                errorMessage={lastNameError}
                autoFocusOnError
            />
            <div className="button-row">
                <TextButton variant="primary" onClick={handleNext}>Next</TextButton>
            </div>
        </>
    );
};

export default Step1Name;
