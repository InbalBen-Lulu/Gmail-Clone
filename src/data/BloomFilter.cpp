#include "BloomFilter.h"

// Initializes the bloom filter with a reference to storage and a zeroed bit array
BloomFilter::BloomFilter(BloomStorage& storage, int arraySize)
    : storage(storage), bitArray(arraySize, 0) {}

// Sets all the specified indices in the bit array to 1,
// and stores them via the storage layer
void BloomFilter::add(const std::vector<int>& hashBits) {
    for (size_t i = 0; i < hashBits.size(); ++i) {
        if (hashBits[i] == 1) {
            bitArray[i] = 1;
        }
    }
    storage.update(hashBits);
}


// Returns true if all specified indices in the bit array are 1,
// otherwise returns false
bool BloomFilter::contain(const std::vector<int>& hashBits) const {
    for (size_t i = 0; i < hashBits.size(); ++i) {
        if (hashBits[i] == 1 && bitArray[i] == 0) {
            return false;
        }
    }
    return true;
}