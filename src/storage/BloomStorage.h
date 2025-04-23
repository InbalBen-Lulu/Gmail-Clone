#pragma once
#include <string>

class BloomStorage {
private:
    std::string path;
    int arraySize;
public:
    BloomStorage(int arraySize);
    BloomStorage();
    void update(const std::vector<int>& bitArray);
    std::vector<int> load();
    std::vector<int> init();
    int getArraySize() const;
};
