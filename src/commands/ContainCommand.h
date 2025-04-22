#pragma once
#include "ICommand.h"
#include "BloomFilter.h"
#include "Blacklist.h"
#include "Hash.h"

class ContainCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    Blacklist& blacklist;
public:
    ContainCommand(BloomFilter& bloom, Blacklist& bl);
    void execute(const std::string& url, Hash& hash) override;
};
