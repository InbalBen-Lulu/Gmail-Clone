#pragma once
#include <string>
#include <set>
#include "../utils/Url.h"

// Handles persistent storage of blacklisted URLs
class BlackListStorage {
private:
    std::string path;
    bool newFile;
public:
    // Constructor: initialize storage with option to create a new file
    BlackListStorage(bool newFile);

    // Add a URL to the blacklist file
    void add(const Url& url);

    // Initialize the blacklist file (create or clear if necessary)
    void init();

    // Return whether a new file was initialized
    bool getNewFile() const;

    // Load all blacklisted URLs from the storage file
    std::set<Url> load();

    // Removes all occurrences of the given URL from persistent storage and memory
    void deleteUrl(const Url& url);
};

