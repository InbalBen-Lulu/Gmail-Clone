#pragma once
#include "..\commands\ICommand.h"
#include "..\data\BloomFilter.h"
#include "..\data\BlackList.h"
#include "..\utils\Hash"
#include "..\utils\Url.h"
#include "..\io\IIOHandler.h"

class ContainCommand : public ICommand {
private:
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    ContainCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
};
