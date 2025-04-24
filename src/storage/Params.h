#pragma once
#include <string>
#include <vector>

class Params {
private:
    std::string path;
    bool newFile;
    int arraySize;
    std::vector<int> hashArray;

    std::string buildParamLine() const;
    std::string readFile() const;
    void writeFile(const std::string& content) const;
    bool fileExists() const;

public:
    Params(int arraySize, const std::vector<int>& hashArray, const std::string& path = "../data/params.txt");
    bool getNewFile() const;
};
