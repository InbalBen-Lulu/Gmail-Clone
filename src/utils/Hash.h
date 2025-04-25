#pragma once
#include <vector>
#include <string>
#include "Url.h"

class Hash {
private:
    std::vector<int> hashArray;
    int bitArraySize;
    std::hash<std::string> hasher;
public:
    Hash(std::vector<int>& hashArray, int bitArraySize);
    std::vector<int> execute(const Url& url) const;
};

