#pragma once
#include <vector>
#include <string>
#include <memory>

class Url;

class Hash {
private:
    std::unique_ptr<int[]> hashArray;
    int arraySize;
public:
    Hash(std::unique_ptr<int[]> hashArrayת int arraySize);
    std::vector<int> execute(const Url& url) const;
};

