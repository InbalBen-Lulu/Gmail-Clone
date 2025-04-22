#include "AddCommand.h"

AddCommand::AddCommand(BloomFilter& bloom, Blacklist& bl)
    : bloomFilter(bloom), blacklist(bl) {}

void AddCommand::execute(const std::string& url, Hash& hash) {
    // TODO: Implement adding logic
}
