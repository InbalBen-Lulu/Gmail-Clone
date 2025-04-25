#pragma once
#include "ICommand.h"
#include "..\data\BloomFilter.h"
#include "..\data\BlackList.h"
#include "..\utils\Hash"
#include "..\utils\Url.h"

class AddCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    AddCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
};
