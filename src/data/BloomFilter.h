#pragma once
#include <string>
#include <vector>
#include "../storage/BloomStorage.h"

// Represents a Bloom filter for approximate set membership testing
class BloomFilter {
private:
    BloomStorage& storage;
    std::vector<int> bitArray;
public:
    // Constructor: initialize the Bloom filter with given storage and array size
    BloomFilter(BloomStorage& storage, size_t arraySize);

    // Check if all bits corresponding to the hash results are set
    bool contain(const std::vector<int>& hashResults) const;

    // Set the bits corresponding to the hash results
    void add(const std::vector<int>& hashResults);
};
