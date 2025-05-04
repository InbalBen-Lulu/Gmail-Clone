#include "PostCommand.h"

// Constructor: initializes PostCommand with references to a BloomFilter and a BlackList
PostCommand::PostCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

/*
 * Executes the PostCommand:
 * - Hashes the given URL
 * - Adds the resulting hash bits to the BloomFilter
 * - Adds the URL to the BlackList
 * - Returns "201 Created" upon successful addition
 */
std::string PostCommand::execute(const Url& url, Hash& hash) {
    std::vector<int> hashResult = hash.execute(url); // Generate hash bits for the URL
    bloomFilter.add(hashResult);
    blackList.add(url);
    return "201 Created";
}
