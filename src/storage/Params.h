#pragma once
#include <string>

class Params {
private:
    std::string path;
    bool newFile;
    int arraySize;
    int hashCount;
public:
    Params(int arraySize, int* configArray);
    void init();
    void load();
    bool getNewFile() const;
};
