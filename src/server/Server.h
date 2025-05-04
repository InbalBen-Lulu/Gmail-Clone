#pragma once
#include <memory>
#include <map>
#include <vector>
#include <string>
#include "../data/BloomFilter.h"
#include "../storage/BloomStorage.h"
#include "../data/BlackList.h"
#include "../storage/BlackListStorage.h"
#include "../commands/ICommand.h"
#include "../utils/Hash.h"
#include "../parser/InputParser.h"

class Server {
private:
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<BlackList> blackList;
    std::map<std::string, std::unique_ptr<ICommand>> commands;
    std::shared_ptr<Hash> hash;
    std::unique_ptr<BlackListStorage> blackListStorage;
    std::unique_ptr<BloomStorage> bloomStorage;
    int port;

    void setup();
    void handleClient(int clientSocket);
    void initSystem(size_t arraySize, const std::vector<int>& hashArray);
    void registerCommands();

public:
    Server(inr port, size_t arraySize, std::vector<int>& hashArray);
    void run();
};