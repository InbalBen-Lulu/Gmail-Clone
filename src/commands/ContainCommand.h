#pragma once
#include "ICommand.h"
#include "BloomFilter.h"
#include "BlackList.h"
#include "Hash.h"
#include "Url.h"

class ContainCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    ContainCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash) override;
};
