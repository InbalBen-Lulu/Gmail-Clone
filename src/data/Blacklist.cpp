#include "Blacklist.h"

Blacklist::Blacklist(BlacklistStorage& storage) : storage(storage) {}

void Blacklist::add(const std::string& url) {
    // TODO: Add to blacklist and storage
}

bool Blacklist::contains(const std::string& url) {
    return false; // TODO: Check if URL exists
}
