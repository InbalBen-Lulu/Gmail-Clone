#pragma once
#include <vector>
#include <string>
#include "Url.h"

/*
 * Hash class:
 * Responsible for generating hash-based bit indices for URLs
 * according to a configurable sequence of repeated hash applications.
 */
class Hash {
private:
    std::vector<int> hashArray;
    size_t bitArraySize;            // Size of the bit array (Bloom filter size)
    std::hash<std::string> hasher;  // Standard C++ string hasher
public:
    // Constructor: initializes the hash configuration and bit array size
    Hash(const std::vector<int>& hashArray, size_t bitArraySize);

    // Executes hashing logic on the given URL and returns a bit vector
    std::vector<int> execute(const Url& url) const;
};