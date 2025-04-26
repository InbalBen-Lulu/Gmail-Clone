#include <functional>
#include <string>
#include <vector>
#include "Hash.h"

// Constructor: store hash configuration and bit array size
Hash::Hash(std::vector<int>& hashArray, size_t bitArraySize)
    : hashArray(std::move(hashArray)), bitArraySize(bitArraySize), hasher(std::hash<std::string>()) {
}

// Generate hash indices for the given URL
std::vector<int> Hash::execute(const Url& url) const {
    std::vector<int> results(bitArraySize, 0);
    std::string base = url.getUrlPath();

    for (size_t i = 0; i < hashArray.size(); i++) {
        if (hashArray[i] <= 0) 
            continue;
        size_t h = hasher(base);

        // Apply hash function multiple times if configured
        for (int j = 1; j < hashArray[i]; j++) {
            base = std::to_string(h);
            h = hasher(base);
        }

        int index = static_cast<int>(h % bitArraySize);  // map to bit array index
        results[index] = 1;
    }

    return results;
}