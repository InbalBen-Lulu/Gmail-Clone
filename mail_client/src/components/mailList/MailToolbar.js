import './MailToolbar.css';
import Icon from '../../assets/icons/Icon';
import { SmallIconButton } from '../common/button/IconButtons';

/**
 * MailToolbar appears at the top of the mail list.
 * Includes refresh button and pagination.
 */
const MailToolbar = ({
  onRefresh,
  onPrevPage,
  onNextPage,
  currentRange = '1-50',
  totalCount = 0
}) => {
  return (
    <div className="mail-toolbar">
      <div className="mail-toolbar-left">
        <SmallIconButton icon={<Icon name="refresh" />} onClick={onRefresh} ariaLabel="Refresh" />
      </div>

      <div className="mail-toolbar-right">
        <span className="mail-toolbar-range">
          {currentRange} of {totalCount.toLocaleString()}
        </span>
        <SmallIconButton icon={<Icon name="arrow_back" />} onClick={onPrevPage} ariaLabel="Previous" />
        <SmallIconButton icon={<Icon name="arrow_forward" />} onClick={onNextPage} ariaLabel="Next" />
      </div>
    </div>
  );
};

export default MailToolbar;
