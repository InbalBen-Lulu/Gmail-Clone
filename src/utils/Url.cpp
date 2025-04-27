#include "Url.h"
#include <regex>

// Constructor: Initializes a Url instance with the given URL string.
Url::Url(const std::string& url) : urlPath(url) {}

// Returns the stored URL path as a string.
std::string Url::getUrlPath() const {
    return urlPath;
}

/*
 * Less-than operator overload to allow Url objects to be used in ordered containers.
 * Compares based on the urlPath string.
 */
bool Url::operator<(const Url& other) const {
    return urlPath < other.urlPath;
}
