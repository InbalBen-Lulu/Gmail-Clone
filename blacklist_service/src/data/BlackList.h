#pragma once
#include <set>
#include <string>
#include "../utils/Url.h"
#include "../storage/BlackListStorage.h"

// Manages a collection of blacklisted URLs with persistent storage support
class BlackList {
private:
    BlackListStorage& storage;
    std::set<Url> urls;

public:
    // Constructor: initialize the blacklist with a reference to its storage
    BlackList(BlackListStorage& storage);

    // Add a URL to the blacklist and update the storage
    void add(const Url& url);

    // Check if a URL exists in the blacklist
    bool contains(const Url& url) const;

    // Remove a URL from the blacklist and update the storage
    void deleteUrl(const Url& url);
};
