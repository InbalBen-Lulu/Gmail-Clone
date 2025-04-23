#pragma once
#include <string>
#include <vector>
#include "BloomStorage.h"

class BloomFilter {
private:
    BloomStorage& storage;
    std::vector<int> bitArray;
public:
    BloomFilter(BloomStorage& storage);
    bool contain(const std::vector<int>& hashResults) const;
    void add(const std::vector<int>& hashResults);
};
