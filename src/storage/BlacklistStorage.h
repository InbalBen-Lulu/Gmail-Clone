#pragma once
#include <string>
#include <set>
#include "../utils/Url.h"

class BlackListStorage {
private:
    std::string path;
    bool newFile;
public:
    BlackListStorage(bool newFile);
    void add(const Url& url);
    void init();
    bool getNewFile() const;
    std::set<Url> load();
};

