#pragma once
#include <string>

class Params {
private:
    std::string path;
    bool newFile;
    int arraySize;
    int* hashArray;

    std::string buildParamLine() const;
    std::string readFile() const;
    void writeFile(const std::string& content) const;
    bool fileExists() const;

public:
    Params(int arraySize, int* hashArray);
    bool getNewFile() const;
};
