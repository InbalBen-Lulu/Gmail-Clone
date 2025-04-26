#include "ContainCommand.h"

ContainCommand::ContainCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

void ContainCommand::execute(const Url& url, Hash& hash, IIOHandler& io) {
    std::vector<int> hashBits = hash.execute(url);  // Bit vector from hash

    // Check if all required bits are also set in the Bloom filter
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

