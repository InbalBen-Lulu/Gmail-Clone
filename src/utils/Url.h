#pragma once
#include <string>

// Represents a URL and provides utilities for validation and comparison.
class Url {
private:
    std::string urlPath;
public:
    // Constructor: Initializes the Url object with the given URL string.
    Url(const std::string&);

    // Returns the stored URL path as a string.
    std::string getUrlPath() const;

    /*
     * Less-than operator overload for comparing Url objects.
     * Compares based on the urlPath string.
     */
    bool operator<(const Url& other) const;
};
