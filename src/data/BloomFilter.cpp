#include "BloomFilter.h"

// Initializes the bloom filter with a reference to storage and a zeroed bit array
BloomFilter::BloomFilter(BloomStorage& storage, int arraySize)
    : storage(storage), bitArray(arraySize, 0) {}

// Sets all the specified indices in the bit array to 1,
// and stores them via the storage layer
void BloomFilter::add(const std::vector<int>& hashResults) {
    for (int index : hashResults) {
        if (index >= 0 && index < static_cast<int>(bitArray.size())) {
            bitArray[index] = 1;
        }
    }

    storage.add(hashResults);
}

// Returns true if all specified indices in the bit array are 1,
// otherwise returns false
bool BloomFilter::contain(const std::vector<int>& hashResults) const {
    for (int index : hashResults) {
        if (index < 0 || index >= static_cast<int>(bitArray.size()) || bitArray[index] == 0) {
            return false;
        }
    }
    return true;
}
