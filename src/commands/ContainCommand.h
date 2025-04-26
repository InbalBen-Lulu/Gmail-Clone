#pragma once
#include "ICommand.h"
// #include "../data/BloomFilter.h"
// #include "../data/BlackList.h"
// #include "../utils/Hash.h"
// #include "../utils/Url.h"

class ContainCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    ContainCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
};
