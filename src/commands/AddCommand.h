#pragma once
#include "ICommand.h"
#include "BloomFilter.h"
#include "Blacklist.h"
#include "Hash.h"

class AddCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    Blacklist& blacklist;
public:
    AddCommand(BloomFilter& bloom, Blacklist& bl);
    void execute(const std::string& url, Hash& hash) override;
};
