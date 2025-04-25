#pragma once
#include <set>
#include <string>
#include "../utils/Url.h"
#include "../storage/BlackListStorage.h"

class BlackList {
private:
    BlackListStorage& storage;
    std::set<Url> urls;
public:
    BlackList(BlackListStorage& storage);
    void add(const Url& url);
    bool contains(const Url& url) const;
};
