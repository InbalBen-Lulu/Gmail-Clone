#include "BlackList.h"

// Initializes the BlackList with an empty set and links it to the given storage
BlackList::BlackList(BlackListStorage& storage)
    : storage(storage), urls() {}

// Adds a URL to the in-memory set and updates the persistent storage
void BlackList::add(const Url& url) {
    urls.insert(url);
    storage.add(url);  // Persist to file via the storage layer
}

// Checks whether the given URL is in the in-memory set
bool BlackList::contains(const Url& url) const {
    return urls.find(url) != urls.end();
}
