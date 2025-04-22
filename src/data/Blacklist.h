#pragma once
#include <set>
#include <string>
#include "Url.h"
#include "BlacklistStorage.h"

class Blacklist {
private:
    BlacklistStorage& storage;
    std::set<Url> urls;
public:
    Blacklist(BlacklistStorage& storage);
    void add(const std::string&);
    bool contains(const std::string&);
};
