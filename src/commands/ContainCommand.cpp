#include "ContainCommand.h"

ContainCommand::ContainCommand(BloomFilter& bloom, BlackList& bl, IIOHandler& io)
    : bloomFilter(bloom), blackList(bl), lastResult(2, false) {}

void ContainCommand::execute(const Url& url, Hash& hash) {
    std::vector<int> hashBits = hash.execute(url);  // Bit vector from hash

    // Check if all required bits are also set in the Bloom filter
    bool allBitsOn = bloomFilter.contain(hashBits);
    lastResult[0] = allBitsOn;

    if (!allBitsOn) {
        // If not all bits are set, no need to check the blacklist
        lastResult[1] = false;
        return;
    }

    // If all bits are set, check actual presence in the blacklist
    lastResult[1] = blackList.contains(url);

}

const std::vector<bool>& ContainCommand::getLastResult() const {
    return lastResult;
}
