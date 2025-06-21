import { useNavigate } from 'react-router-dom';

/**
 * Step5Photo allows the user to upload a profile picture.
 * This step is optional and includes "Skip" and "Continue" buttons.
 */
const Step5Photo = () => {
  const navigate = useNavigate();

  const handleSkip = () => {
    navigate('/mails/inbox');
  };

  const handleContinue = () => {
    // In a real flow, you'd probably upload the image before navigating.
    navigate('/mails/inbox');
  };

  return (
    <div className="step5-photo-container">
        <span>Upload photo</span>
    </div>
  );
};

export default Step5Photo;
