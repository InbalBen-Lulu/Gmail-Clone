import Textbox from '../../common/input/textBox/TextBox';
import DropdownList from '../../common/input/DropdownList';
import TextButton from '../../common/button/TextButton';
import { isValidDate } from './utils/dateUtils';
import ErrorMessage from '../../common/input/ErrorMessage';
import { useState } from 'react';
import './Step2Birth.css';

/**
 * Step2Birth component collects and validates the user's birthdate and gender.
 * It ensures all fields are filled and the date is valid before proceeding.
 */
const Step2Birth = ({
    month,
    day,
    year,
    gender,
    setMonth,
    setDay,
    setYear,
    setGender,
    monthError,
    dayError,
    yearError,
    genderError,
    setMonthError,
    setDayError,
    setYearError,
    setGenderError,
    onNext,
    months,
    genders
}) => {
    const [birthdayErrorMessage, setBirthdayErrorMessage] = useState('');

    const handleNext = () => {
        let hasError = false;

        const dayNum = +day;
        const yearNum = +year;

        const isDayMissing = !day || day.trim() === '';
        const isMonthMissing = !month || month.trim() === '';
        const isYearMissing = !year || year.trim() === '';
        const isGenderMissing = !gender || gender.trim() === '';

        const isIncompleteDate = isDayMissing || isMonthMissing || isYearMissing;

        const isDayValid = !isNaN(dayNum) && dayNum >= 1 && dayNum <= 31;
        const isYearValid = !isNaN(yearNum) && yearNum >= 1900 && yearNum <= new Date().getFullYear();
        const isDateValid = isDayValid && isYearValid && isValidDate(dayNum, month, yearNum);

        if (isIncompleteDate) {
            setMonthError(' ');
            setDayError(' ');
            setYearError(' ');
            setBirthdayErrorMessage('Please fill in a complete birthday');
            hasError = true;
        } else if (!isDateValid) {
            setMonthError(' ');
            setDayError(' ');
            setYearError(' ');
            setBirthdayErrorMessage('Please enter a valid date');
            hasError = true;
        } else {
            setMonthError('');
            setDayError('');
            setYearError('');
            setBirthdayErrorMessage('');
        }

        if (isGenderMissing) {
            setGenderError('Please select your gender');
            hasError = true;
        } else {
            setGenderError('');
        }

        if (!hasError) {
            onNext();
        }
    };

    return (
        <>
            <div className="birthday-row">
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                    <DropdownList
                        label="Month"
                        name="month"
                        value={month}
                        onChange={(e) => setMonth(e.target.value)}
                        options={months}
                        variant='floating'
                        size='quarter'
                        isInvalid={!!monthError}
                        isValid={!monthError}
                        errorMessage={monthError}
                    />
                </div>
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                    <Textbox
                        label="Day"
                        name="day"
                        value={day}
                        onChange={(e) => setDay(e.target.value)}
                        variant='floating'
                        isInvalid={!!dayError}
                        isValid={!dayError}
                        errorMessage={dayError}
                        size="quarter"
                        maxLength={2}
                        autoFocusOnError
                    />
                </div >
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                    <Textbox
                        label="Year"
                        name="year"
                        value={year}
                        onChange={(e) => setYear(e.target.value)}
                        variant='floating'
                        isInvalid={!!yearError}
                        isValid={!yearError}
                        errorMessage={yearError}
                        size="quarter"
                        maxLength={4}
                        autoFocusOnError
                    />
                </div >
            </div >
            {birthdayErrorMessage && (
                <ErrorMessage message={birthdayErrorMessage} />

            )}
            <DropdownList
                label="Gender"
                name="gender"
                value={gender}
                onChange={(e) => setGender(e.target.value)}
                options={genders}
                variant='floating'
                isInvalid={!!genderError}
                isValid={!genderError}
                errorMessage={genderError}
                autoFocusOnError
            />
            <div className="button-row">
                <TextButton variant="primary" onClick={handleNext}>Next</TextButton>
            </div>
        </>
    );
};

export default Step2Birth;
