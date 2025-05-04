#pragma once
#include "ICommand.h"

class DeleteCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    DeleteCommand(BloomFilter& bloom, BlackList& bl);
    string execute(const Url& url, Hash& hash) override;
};
    