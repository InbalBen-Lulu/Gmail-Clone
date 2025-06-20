import { FiMail } from 'react-icons/fi';
import Icon from '../../../../assets/icons/Icon';
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
          <div className="search-result-item" key={mail.id}>
            <div className="result-date">{mail.date}</div>
            <div className="result-content">
              <div className="result-subject">{mail.subject}</div>
              <div className="result-from">{mail.from}</div>
            </div>
            <div className="result-icon">
              <FiMail className="mail-icon" />
            </div>
          </div>
        ))
      )}

      {/* Final row shown if search text is entered */}
      {searchText && (
        <div className="search-show-all">
          <Icon name="search" />
          <span>All search results for "{searchText}"</span>
        </div>
      )}
    </div>
  );
};

export default SearchResultsDropdown;
