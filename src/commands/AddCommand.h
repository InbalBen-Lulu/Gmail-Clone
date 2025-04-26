#pragma once
#include "ICommand.h"

class AddCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    AddCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
};
