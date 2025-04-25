#include "AddCommand.h"

AddCommand::AddCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

void AddCommand::execute(const Url& url, Hash& hash, IIOHandler& io) {
    std::vector<int> hashResult = hash.execute(url);
    bloomFilter.update(hashResult);
    blackList.add(url);
}
