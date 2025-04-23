#pragma once
#include <set>
#include <string>
#include "Url.h"
#include "BlackListStorage.h"

class BlackList {
private:
    BlackListStorage& storage;
    std::set<Url> urls;
public:
    BlackList(BlackListStorage& storage);
    void add(const std::string&);
    bool contains(const std::string&);
};
