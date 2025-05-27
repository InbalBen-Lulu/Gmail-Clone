#pragma once
#include <string>
#include <mutex>
#include "../data/BloomFilter.h"
#include "../data/BlackList.h"
#include "../utils/Hash.h"
#include "../utils/Url.h"

// Interface for command classes that operate on URLs using hash functions
class ICommand {
public:
    // Execute the command with the given URL and hash function
    virtual std::string execute(const Url& url, Hash& hash) = 0;
    
     // Virtual destructor to ensure proper cleanup in derived classes
    virtual ~ICommand() = default;
};
