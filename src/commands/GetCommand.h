#pragma once
#include "ICommand.h"

// Command that checks if a given URL exists in the Bloom filter and the blacklist
class GetCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    // Constructor: initialize with references to Bloom filter and blacklist
    GetCommand(BloomFilter& bloom, BlackList& bl);

    /*
     * Execute the get command:
     * - Returns a response in HTTP-like format.
     * - If the URL is not in the Bloom filter: returns "200 OK\n\nfalse"
     * - If the URL is in the Bloom filter:
     *      - returns "200 OK\n\ntrue true" if it is also in the blacklist
     *      - returns "200 OK\n\ntrue false" if not
     */
    std::string execute(const Url& url, Hash& hash) override;
};
