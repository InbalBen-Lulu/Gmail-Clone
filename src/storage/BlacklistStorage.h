#pragma once
#include <string>
#include <set>
#include "../utils/Url.h"

class BlacklistStorage {
private:
    std::string path;
    bool newFile;
public:
    BlacklistStorage(bool newFile);
    void add(const Url& url);
    void init();
    bool getNewFile() const;
    std::set<Url> load();
};

