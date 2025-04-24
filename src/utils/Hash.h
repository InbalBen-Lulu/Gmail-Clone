#pragma once
#include <vector>
#include <string>
#include "Url.h"

class Hash {
private:
    std::vector<int> hashArray;
    int bitArraySize;
public:
    Hash(const std::vector<int>& hashArray, int bitArraySize);
    std::vector<int> execute(const Url& url) const;
};

