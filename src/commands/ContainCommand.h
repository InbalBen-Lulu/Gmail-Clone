#pragma once
#include "ICommand.h"

// Command that checks if a given URL exists in the Bloom filter and the blacklist
class ContainCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    // Constructor: initialize with references to Bloom filter and blacklist
    ContainCommand(BloomFilter& bloom, BlackList& bl);

    /* Execute the containment command: 
     first check the Bloom filter, then verify in the blacklist if necessary  */
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
};
