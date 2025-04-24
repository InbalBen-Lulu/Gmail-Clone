#include "Blacklist.h"

// Initializes the blacklist with an empty set and links it to the given storage
Blacklist::Blacklist(BlacklistStorage& storage)
    : storage(storage), urls() {}

// Adds a URL to the in-memory set and updates the persistent storage
void Blacklist::add(const Url& url) {
    urls.insert(url);
    storage.add(url);  // Persist to file via the storage layer
}

// Checks whether the given URL is in the in-memory set
bool Blacklist::contains(const Url& url) const {
    return urls.find(url) != urls.end();
}
