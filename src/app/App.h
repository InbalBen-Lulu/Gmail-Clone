#pragma once
#include <memory>
#include <map>
#include <string>
#include "../io/IIOHandler.h"
#include "../data/BloomFilter.h"
#include "../storage/BloomStorage.h"
#include "../data/BlackList.h"
#include "../storage/BlackListStorage.h"
#include "../commands/ICommand.h"
#include "../utils/Hash.h"

class App {
private:
    std::unique_ptr<IIOHandler> io;
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<BlackList> blackList;
    std::map<int, std::unique_ptr<ICommand>> commands;
    std::shared_ptr<Hash> hash;
    std::unique_ptr<BlackListStorage> blackListStorage;
    std::unique_ptr<BloomStorage> bloomStorage;
    void initSystem(int arraySize, std::vector<int>& hashArray);
public:
    App();
    void run();
};
