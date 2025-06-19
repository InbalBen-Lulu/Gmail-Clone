import { useState } from 'react';
import MailList from './MailList';
import MailDetails from './MailDetails';
import MailToolbar from './MailToolbar';
import { SmallIconButton } from '../common/button/IconButtons';
import Icon from '../../assets/icons/Icon';

const MailPanel = ({ mails, allLabels, onDelete, onStarToggle, onSpam }) => {
  const [selectedMailId, setSelectedMailId] = useState(null);

  const selectedMail = mails.find(m => m.id === selectedMailId);

  // Dummy values for pagination display
  const currentRange = `1-${Math.min(50, mails.length)}`;
  const totalCount = mails.length;

  return (
    <div className="mail-panel">
      {!selectedMail ? (
        <>
          <MailToolbar
            currentRange={currentRange}
            totalCount={totalCount}
            onRefresh={() => alert('Refresh pressed')}
            onPrevPage={() => alert('Previous page')}
            onNextPage={() => alert('Next page')}
          />
          <MailList
            mails={mails}
            allLabels={allLabels}
            onDelete={onDelete}
            onStarToggle={onStarToggle}
            onRightClick={(e, mailId) => e.preventDefault()}
            onClick={setSelectedMailId}
          />
        </>
      ) : (
        <>
          <div className="mail-details-topbar">
            <SmallIconButton
              icon={<Icon name="arrow-back2" />}
              ariaLabel="Back"
              onClick={() => setSelectedMailId(null)}
            />
          </div>
          <MailDetails
            mail={selectedMail}
            allLabels={allLabels}
            onDelete={onDelete}
            onStarToggle={onStarToggle}
            onBack={() => setSelectedMailId(null)}
            onSpam={onSpam}
          />
        </>
      )}
    </div>
  );
};

export default MailPanel;
