#pragma once
#include <memory>
#include <map>
#include "../io/IIOHandler.h"
#include "../data/BloomFilter.h"
#include "../storage/BloomStorage.h"
#include "../data/BlackList.h"
#include "../storage/BlackListStorage.h"
#include "../commands/ICommand.h"
#include "../utils/Hash.h"

/*
 * App class:
 * Main application controller that manages input/output, system initialization,
 * and execution of user commands.
 */
class App {
private:
    std::unique_ptr<IIOHandler> io;
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<BlackList> blackList;
    std::map<int, std::unique_ptr<ICommand>> commands;
    std::shared_ptr<Hash> hash;
    std::unique_ptr<BlackListStorage> blackListStorage;
    std::unique_ptr<BloomStorage> bloomStorage;

     // Initializes all system components based on the initial input parameters
    void initSystem(size_t arraySize, std::vector<int>& hashArray);

public:
    // Constructor: sets up the App with a console-based IO handler
    App();

    // Main loop: reads user commands and executes them
    void run();
};
