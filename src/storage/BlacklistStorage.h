#pragma once
#include <string>

class BlackListStorage {
private:
    std::string path;
public:
    BlackListStorage(bool newFile);
    void add(const std::string&);
    void load();
    void init();
};
