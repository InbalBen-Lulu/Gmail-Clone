#pragma once
#include <string>
#include <vector>

class Params {
private:
    std::string path;
    bool newFile;
    size_t arraySize;
    std::vector<int> hashArray;

    std::string buildParamLine() const;
    std::string readFile() const;
    void writeFile(const std::string& content) const;
    bool fileExists() const;

public:
    Params(size_t arraySize, const std::vector<int>& hashArray);
    bool getNewFile() const;
};
