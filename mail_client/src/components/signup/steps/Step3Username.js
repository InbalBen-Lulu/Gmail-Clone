import Textbox from '../../common/input/TextBox';
import TextButton from '../../common/button/TextButton';

const Step3Username = ({
  gmail,
  setGmail,
  gmailError,
  setGmailError,
  onNext
}) => {
  const handleNext = () => {
    if (!gmail.trim()) {
      setGmailError('Username is required');
    } else {
      setGmailError('');
      onNext();
    }
  };

  return (
    <>
      <Textbox
        label="Username"
        name="gmail"
        value={gmail}
        onChange={(e) => setGmail(e.target.value)}
        suffix="@gmail.com"
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
