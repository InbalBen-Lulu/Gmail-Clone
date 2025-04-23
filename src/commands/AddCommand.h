#pragma once
#include "ICommand.h"
#include "BloomFilter.h"
#include "BlackList.h"
#include "Hash.h"
#include "Url.h"

class AddCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    AddCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash) override;
};
