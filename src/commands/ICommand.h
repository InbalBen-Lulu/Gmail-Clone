#pragma once
#include <string>
#include "../data/BloomFilter.h"
#include "../data/BlackList.h"
#include "../utils/Hash.h"
#include "../utils/Url.h"
#include "../io/IIOHandler.h"

// Interface for command classes that operate on URLs using hash functions and I/O
class ICommand {
public:
    // Execute the command with the given URL, hash function, and I/O handler
    virtual void execute(const Url& url, Hash& hash, IIOHandler& io) = 0;
    
     // Virtual destructor to ensure proper cleanup in derived classes
    virtual ~ICommand() = default;
};
