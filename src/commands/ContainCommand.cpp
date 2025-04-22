#include "ContainCommand.h"

ContainCommand::ContainCommand(BloomFilter& bloom, Blacklist& bl)
    : bloomFilter(bloom), blacklist(bl) {}

void ContainCommand::execute(const std::string& url, Hash& hash) {
    // TODO: Check bloom and blacklist
}
