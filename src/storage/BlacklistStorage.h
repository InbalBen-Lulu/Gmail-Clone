#pragma once
#include <string>

class BlacklistStorage {
private:
    std::string path;
public:
    BlacklistStorage(bool newFile);
    void add(const std::string&);
    void load();
    void init();
};
