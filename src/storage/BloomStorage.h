#pragma once
#include <string>

class BloomStorage {
private:
    std::string path;
public:
    BloomStorage(bool newFile);
    void update();
    void load();
    void init();
};
