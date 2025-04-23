#include "Hash.h"

Hash::Hash(std::unique_ptr<int[]> hashArray, int arraySize): hashArray(std::move(hashArray)), arraySize(arraySize) {}

std::vector<int> Hash::execute(const Url& url) const {
    
}
