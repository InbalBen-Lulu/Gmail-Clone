#pragma once
#include <string>
#include "IStorage.h"

class Params : public IStorage {
private:
    std::string path;
    bool newFile;
    int arraySize;
    int hashCount;
public:
    Params(int arraySize, int* configArray);
    void init() override;
    void load() override;
    bool getNewFile() const;
};
