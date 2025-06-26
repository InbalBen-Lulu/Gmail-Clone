import { FiMail } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import Icon from '../../../../assets/icons/Icon';
import { formatMailDate } from '../../../../utils/dateUtils';
import './SearchResultsDropdown.css';

/**
 * Dropdown list of mail search results
 *
 * Props:
 * - results: array of mail objects { id, subject, from, date }
 * - visible: boolean – whether the dropdown is shown
 * - searchText: string – current input value from search bar
 */
const SearchResultsDropdown = ({ results, visible, searchText }) => {
  const navigate = useNavigate();

  /**
   * Navigates to the full search results page using the current search text.
   */
  const handleShowAllResultsClick = () => {
    navigate(`/mails/search-${encodeURIComponent(searchText)}`);
  };

  /**
   * Navigates to a specific mail result by its ID.
   * 
   * @param {string} mailId - ID of the selected mail.
   */
  const handleResultClick = (mailId) => {
    const encodedQuery = encodeURIComponent(searchText);
    navigate(`/mails/search-${encodedQuery}/${mailId}`);
  };

  if (!visible) return null; // Do not render if not visible

  return (
    <div className="search-dropdown">
      {/* No results message */}
      {results.length === 0 ? (
        <div className="search-no-results">
          No recent items match your search.
        </div>
      ) : (
        // List of search result items
        results.map((mail) => (
          <div
            className="search-result-item"
            key={mail.id}
            onClick={() => handleResultClick(mail.id)}
          >
            <div className="result-date">{formatMailDate(mail.sentAt)}</div>
            <div className="result-content">
              <div className="result-subject">{mail.subject}</div>
              <div className="result-from">{mail.from.name}</div>
            </div>
            <div className="result-icon">
              <FiMail className="mail-icon" />
            </div>
          </div>
        ))
      )}

      {/* Final row – link to all results */}
      {searchText && (
        <div
          className="search-show-all"
          onClick={handleShowAllResultsClick}
        >
          <Icon name="search" />
          <span>All search results for "{searchText}"</span>
        </div>
      )}
    </div>
  );
};

export default SearchResultsDropdown;
