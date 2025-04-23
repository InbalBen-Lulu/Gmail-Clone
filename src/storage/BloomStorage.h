#pragma once
#include <string>

class BloomStorage {
private:
    std::string path;
    bool newFile;
public:
    BloomStorage(bool newFile;);
    void update(const std::vector<int>& bitArray);
    std::vector<int> load();
    bool getNewFile() const;
    void init();
};
