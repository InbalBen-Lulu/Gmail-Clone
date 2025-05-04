#pragma once
#include "ICommand.h"

// Command that deletes a given URL from the blacklist if it exists
class DeleteCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    // Constructor: initialize with references to Bloom filter and blacklist
    DeleteCommand(BloomFilter& bloom, BlackList& bl);
    
    /*
     * Execute the delete command:
     * - If the URL exists in the blacklist, remove it and return "204 No Content"
     * - Otherwise, return "404 Not Found"
     */
    std::string execute(const Url& url, Hash& hash) override;
};
    