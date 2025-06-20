import './SearchResultsDropdown.css';

/**
 * Dropdown list of mail search results
 * Props:
 * - results: array of { id, subject, from, date }
 * - visible: boolean – whether the dropdown is shown
 * - searchText: string – the input text, used for final row
 */
const SearchResultsDropdown = ({ results, visible, searchText }) => {
  if (!visible) return null;

  return (
    <div className="search-dropdown">
      {results.length === 0 ? (
        <div className="search-empty-space" />
      ) : (
        results.map((mail) => (
          <div className="search-result-item" key={mail.id}>
            <div className="result-date">{mail.date}</div>       {/* עכשיו יהיה ראשון */}
            <div className="result-content">                     {/* עכשיו שני */}
                <div className="result-subject">{mail.subject}</div>
                <div className="result-from">{mail.from}</div>
            </div>
          </div>
        ))
      )}

      {/* ✅ שורת הסיום */}
      {searchText && (
        <div className="search-show-all">
          All search results for "<span>{searchText}</span>"
        </div>
      )}
    </div>
  );
};

export default SearchResultsDropdown;
