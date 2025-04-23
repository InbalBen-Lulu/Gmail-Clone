#include "Hash.h"

Hash::Hash(std::unique_ptr<int[]> hashArray) : hashArray(std::move(hashArray)) {}

std::vector<int> Hash::execute(const Url& url) const {
    
}
