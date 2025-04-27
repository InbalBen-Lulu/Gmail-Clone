#include "Url.h"
#include <regex>

// Constructor: Initializes a Url instance with the given URL string.
Url::Url(const std::string& url) : urlPath(url) {}

/*
 * Checks if the stored URL matches a standard URL pattern.
 * Returns true if valid, false otherwise.
 */
bool Url::isValid() const {
    static const std::regex urlRegex(
        R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)",
        std::regex::icase
    );
    return std::regex_match(urlPath, urlRegex);
}

// Returns the stored URL path as a string.
std::string Url::getUrlPath() const {
    return urlPath;
}

/*
 * Less-than operator overload to allow Url objects to be used in ordered containers (e.g., std::set).
 * Compares based on the urlPath string.
 */
bool Url::operator<(const Url& other) const {
    return urlPath < other.urlPath;
}
