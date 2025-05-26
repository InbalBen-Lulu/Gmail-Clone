#pragma once
#include <mutex>
#include "ICommand.h"

// Command that adds a given URL to the Bloom filter and the blacklist
class PostCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
    std::mutex& mutex;
public:
    // Constructor: initializes PostCommand with references to a BloomFilter and a BlackList
    PostCommand(BloomFilter& bloom, BlackList& bl, std::mutex& m);

    /*
     * Executes the PostCommand:
     * - Hashes the URL and adds the resulting bits to the Bloom filter
     * - Adds the URL to the blacklist
     * - Returns a "201 Created" message indicating success
     */
    std::string execute(const Url& url, Hash& hash) override;
};
