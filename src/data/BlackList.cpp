#include "BlackList.h"

// Initializes the BlackList with a set loaded from storage or as empty
BlackList::BlackList(BlackListStorage& storage)
    : storage(storage) {
    if (storage.getNewFile()) {
        storage.init();
        urls = {};
    } else {
        urls = storage.load();
    }
}

// Adds a URL to the in-memory set and updates the persistent storage
void BlackList::add(const Url& url) {
    urls.insert(url);
    storage.add(url);  // Persist to file via the storage layer
}

// Checks whether the given URL is in the in-memory set
bool BlackList::contains(const Url& url) const {
    return urls.find(url) != urls.end();
}

/*
 * Removes the given URL from the blacklist.
 * This updates both the in-memory set and the persistent storage.
 */
void BlackList::deleteUrl(const Url& url) {
    urls.erase(url);
    storage.deleteUrl(url);
}
