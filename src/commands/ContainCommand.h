#pragma once
#include "..\commands\ICommand.h"
#include "..\data\BloomFilter.h"
#include "..\data\BlackList.h"
#include "..\utils\Hash"
#include "..\utils\Url.h"

class ContainCommand : public ICommand {
private:
    std::vector<bool> lastResult;  // [0] = found in BloomFilter, [1] = confirmed in BlackList
    BloomFilter& bloomFilter;
    BlackList& blackList;
public:
    ContainCommand(BloomFilter& bloom, BlackList& bl);
    void execute(const Url& url, Hash& hash, IIOHandler& io) override;
    const bool* getLastResult() const;
};
