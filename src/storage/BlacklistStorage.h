#pragma once
#include <string>

class  Url;

class BlackListStorage {
private:
    std::string path;
public:
    BlackListStorage(bool newFile);
    void add(const Url& url);
    std::set<Url> load();
    std::set<Url> init();
};
