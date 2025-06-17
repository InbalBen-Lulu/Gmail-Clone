import { useState } from 'react';
import Textbox from '../../common/input/textBox/TextBox';
import TextButton from '../../common/button/TextButton';

/**
 * Step 1: Collects user's first and last name.
 * Validates that first name is not empty before proceeding.
 */
const Step1Name = ({
    firstName,
    setFirstName,
    lastName,
    setLastName,
    firstNameError,
    setFirstNameError,
    onNext
}) => {
    const [firstNameTouched, setFirstNameTouched] = useState(false);

    const handleNext = () => {
        setFirstNameTouched(true);
        if (!firstName.trim()) {
            setFirstNameError('First name is required');
        } else {
            setFirstNameError('');
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
                label="Last name (optional)"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                variant='floating'
                autoFocusOnError
            />
            <div className="button-row">
                <TextButton variant="primary" onClick={handleNext}>Next</TextButton>
            </div>
        </>
    );
};

export default Step1Name;