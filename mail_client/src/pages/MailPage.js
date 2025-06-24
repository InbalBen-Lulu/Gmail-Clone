import MailPanelContainer from '../components/mailList/MailPanelContainer';
import ComposeForm from '../components/composeMail/ComposeForm';
import { ComposeProvider, useCompose } from '../contexts/ComposeContext';
import { MailProvider } from '../contexts/MailContext';
import { LabelProvider } from '../contexts/LabelContext';
import './MailPage.css';

/**
 * MailPageContent renders the main layout for the mail page,
 * including the mail list panel and compose form if open.
 */
const MailPageContent = () => {
  const { showCompose } = useCompose();

  return (
    <div className="mail-page-wrapper">
      <div className="mail-sidebar-space" />
      <div className="mail-content-area">
        <MailPanelContainer />
        {showCompose && <ComposeForm />}
      </div>
    </div>
  );
};

/**
 * MailPage wraps the content in ComposeProvider, LabelProvider and MailProvider.
 */
const MailPage = () => (
  <ComposeProvider>
    <LabelProvider>
      <MailProvider>
        <MailPageContent />
      </MailProvider>
    </LabelProvider>
  </ComposeProvider>
);

export default MailPage;
