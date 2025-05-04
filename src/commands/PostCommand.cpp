#include "AddCommand.h"

// Constructor: initializes AddCommand with references to a BloomFilter and a BlackList
AddCommand::AddCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

/*
 * Executes the AddCommand:
 * - Hashes the given URL
 * - Adds the resulting hash bits to the BloomFilter
 * - Adds the URL to the BlackList
 */
void AddCommand::execute(const Url& url, Hash& hash, IIOHandler& io) {
    std::vector<int> hashResult = hash.execute(url); // Generate hash bits for the URL
    bloomFilter.add(hashResult);
    blackList.add(url);
}
