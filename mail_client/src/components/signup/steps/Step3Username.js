import Textbox from '../../common/input/textBox/TextBox';
import TextButton from '../../common/button/TextButton';
import { useUserService } from '../../../services/useUserService';


/**
 * Step3Username component allows the user to choose a Gmail username.
 * Validates the input and displays an error if the field is empty.
 */
const Step3Username = ({
    gmail,
    setGmail,
    gmailError,
    setGmailError,
    onNext
}) => {
    const { fetchPublicUser } = useUserService();

    const handleNext = async () => {
        const trimmed = gmail.trim();

        if (!trimmed) {
            setGmailError('Username is required');
            return;
        }

        try {
            const { status } = await fetchPublicUser(trimmed);

            if (status === 200) {
                setGmailError('That username is taken. Try another.');
            } else {
                setGmailError('');
                onNext(); 
            }
        } catch (err) {
            setGmailError('Something went wrong');
        }
    };

    return (
        <>
            <Textbox
                label="Username"
                name="gmail"
                value={gmail}
                onChange={(e) => setGmail(e.target.value)}
                suffix="@mailme.com"
                variant='floating'
                isInvalid={!!gmailError}
                isValid={!gmailError}
                errorMessage={gmailError}
                autoFocusOnError
            />
            <div className="button-row">
                <TextButton variant="primary" onClick={handleNext}>Next</TextButton>
            </div>
        </>
    );
};

export default Step3Username;
