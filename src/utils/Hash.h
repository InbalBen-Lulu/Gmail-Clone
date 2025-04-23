#pragma once
#include <vector>
#include <string>
#include <memory>

class Url;

class Hash {
private:
    std::unique_ptr<int[]> hashArray;
public:
    Hash(std::unique_ptr<int[]> hashArray);
    std::vector<int> execute(const Url& url) const;
};