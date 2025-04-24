#include <functional>
#include <string>
#include <vector>
#include "Hash.h"

Hash::Hash(const std::vector<int>& hashArray, int bitArraySize)
    : hashArray(std::move(hashArray)), bitArraySize(bitArraySize) {
}

std::vector<int> Hash::execute(const Url& url) const {
    std::vector<int> results(bitArraySize, 0);
    std::hash<std::string> hasher;
    std::string base = url.getUrlPath();

    for (size_t i = 0; i < hashArray.size(); i++) {
        if (hashArray[i] <= 0) 
            continue;
        size_t h = hasher(base);

        for (int j = 1; j < hashArray[i]; j++) {
            base = std::to_string(h);
            h = hasher(base);
        }

        int index = static_cast<int>(h % bitArraySize);
        results[index] = 1;
    }

    return result;
}