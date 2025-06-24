import MailList from './MailList';
import MailDetails from './MailDetails';
import MailToolbar from './MailToolbar';
import { useCompose } from '../../contexts/ComposeContext';
import { useNavigate, useParams } from 'react-router-dom';
import { useMemo } from 'react';
import { useMail } from '../../contexts/MailContext';
import './MailPanel.css';

/**
 * MailPanel is the main container for the mail content area.
 * 
 * - If a mail ID is in the URL and the mail exists, it shows the full mail view (MailDetails).
 * - If a draft mail is clicked, it opens the compose window prefilled.
 * - If no mail is selected, it displays the mail list (MailList) and a toolbar (MailToolbar).
 * - Handles pagination, refresh, and routing.
 */
const MailPanel = () => {
  const { openCompose } = useCompose();
  const navigate = useNavigate();
  const { mailId, category } = useParams();
  const mailIdNum = Number(mailId);

  const {
    mails,
    totalCount,
    offset,
    limit,
    onRefresh,
    onNextPage,
    onPrevPage
  } = useMail();

  const selectedMail = useMemo(() => {
    return mails.find(m => m.id === mailIdNum);
  }, [mailIdNum, mails]);

  const handleMailClick = (mail) => {
    if (mail.isDraft) {
      openCompose(mail.to.join(' '), mail.subject, mail.body, true, mail.id);
    } else {
      navigate(`/mails/${category}/${mail.id}`);
    }
  };

  const currentRange = `${offset + 1}-${Math.min(offset + limit, totalCount)}`;

  return (
    <div className="mail-panel">
      {mailIdNum && !selectedMail ? (
        <div className="empty-mail-list">Mail not found</div>
      ) : mailIdNum && selectedMail ? (
        <>
          <MailDetails
            mail={selectedMail}
            onBack={() => navigate(`/mails/${category}`)}
          />
        </>
      ) : (
        <>
          <MailToolbar
            currentRange={currentRange}
            totalCount={totalCount}
            onRefresh={onRefresh}
            onPrevPage={onPrevPage}
            onNextPage={onNextPage}
          />
          {mails.length === 0 ? (
            <div className="empty-mail-list">No mails found</div>
          ) : (
            <MailList
              onClick={(id) => {
                const clickedMail = mails.find(m => m.id === id);
                if (clickedMail) handleMailClick(clickedMail);
              }}
            />
          )}
        </>
      )}
    </div>
  );
};

export default MailPanel;
