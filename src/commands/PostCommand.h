#pragma once
#include "ICommand.h"

// Command that adds a given URL to the Bloom filter and the blacklist
class PostCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    // Constructor: initialize with references to Bloom filter and blacklist
    AddCommand(BloomFilter& bloom, BlackList& bl);

    /*
    Execute the add command: 
    add the URL's hash results to the Bloom filter and store the URL in the blacklist
    */ 
    string execute(const Url& url, Hash& hash) override;
};
