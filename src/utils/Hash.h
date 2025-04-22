#pragma once
#include <vector>
#include <string>
#include <memory>

class Hash {
private:
    std::vector<int> results;
    std::unique_ptr<int[]> hashArray;
public:
    Hash(std::unique_ptr<int[]> hashArray);
    void execute(const std::string&);
    std::vector<size_t> getResults() const;
};

