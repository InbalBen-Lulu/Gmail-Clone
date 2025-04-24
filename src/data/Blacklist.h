#pragma once
#include <set>
#include <string>
#include "../utils/Url.h"
#include "../storage/BlacklistStorage.h"

class Blacklist {
private:
    BlacklistStorage& storage;
    std::set<Url> urls;
public:
    Blacklist(BlacklistStorage& storage);
    void add(const Url& url);
    bool contains(const Url& url) const;
};
