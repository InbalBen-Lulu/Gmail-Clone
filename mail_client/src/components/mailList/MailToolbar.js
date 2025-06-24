import './MailToolbar.css';
import Icon from '../../assets/icons/Icon';
import { SmallIconButton } from '../common/button/IconButtons';
import { useMail } from '../../contexts/MailContext';
import { useSearchParams, useParams } from 'react-router-dom';

/**
 * MailToolbar component.
 * 
 * A toolbar displayed at the top of the mail list.
 * Provides:
 * - Refresh button to reload mails from the server.
 * - Pagination buttons to navigate forward/backward.
 * - Display of the current visible mail range.
 */

const MailToolbar = () => {
  const { totalCount } = useMail();
  const setSearchParams = useSearchParams()[1];
  const { offset, limit, loadMails } = useMail();
  const { category } = useParams();

  const start = totalCount === 0 ? 0 : offset + 1;
  const end = Math.min(offset + limit, totalCount);
  const currentRange = `${start}-${end}`;

  const disablePrev = offset <= 0;
  const disableNext = offset + limit >= totalCount;

  const handleRefresh = () => {
    loadMails(offset, limit, category);
  };

  const handlePrevPage = () => {
    if (!disablePrev) {
      setSearchParams({ offset: offset - limit, limit });
    }
  };

  const handleNextPage = () => {
    if (!disableNext) {
      setSearchParams({ offset: offset + limit, limit });
    }
  };

  return (
    <div className="mail-toolbar">
      <div className="mail-toolbar-left">
        <SmallIconButton
          icon={<Icon name="refresh" />}
          onClick={handleRefresh}
          ariaLabel="Refresh"
        />
      </div>

      <div className="mail-toolbar-right">
        <span className="mail-toolbar-range">
          {currentRange} of {totalCount.toLocaleString()}
        </span>
        <SmallIconButton
          icon={<Icon name="arrow_back" />}
          onClick={handlePrevPage}
          ariaLabel="Previous"
          disabled={disablePrev}
        />
        <SmallIconButton
          icon={<Icon name="arrow_forward" />}
          onClick={handleNextPage}
          ariaLabel="Next"
          disabled={disableNext}
        />
      </div>
    </div>
  );
};

export default MailToolbar;
