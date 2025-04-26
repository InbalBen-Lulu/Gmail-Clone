#include "ContainCommand.h"

// Constructor: initializes ContainCommand with references to a BloomFilter and a BlackList
ContainCommand::ContainCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

/*
 * Executes the ContainCommand:
 * - Hashes the given URL
 * - Checks if all required bits are set in the BloomFilter
 * - If not all bits are set, immediately returns "false"
 * - If all bits are set, checks if the URL is actually in the BlackList
 * - Writes the result to the output (true/false combination)
 */
void ContainCommand::execute(const Url& url, Hash& hash, IIOHandler& io) {
    std::vector<int> hashBits = hash.execute(url); 
    bool allBitsOn = bloomFilter.contain(hashBits);

    if (!allBitsOn) {
        // If not all bits are set, no need to check the blacklist
        io.writeLine("false");
        return;
    }

    // If all bits are set, check actual presence in the blacklist
    bool urlInBlackList = blackList.contains(url);

    // If first result is true, print both results
    io.writeLine("true " + std::string(urlInBlackList ? "true" : "false"));
}

