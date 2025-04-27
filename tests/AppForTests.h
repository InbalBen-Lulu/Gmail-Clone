#pragma once
#include <memory>
#include <map>
#include "../src/io/IIOHandler.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/commands/ICommand.h"
#include "../src/utils/Hash.h"


/*
 * AppForTests class:
 * A specialized version of the App class designed specifically for testing.
 * 
 * This class simulates a limited environment where input is predefined 
 * (typically 4 lines of input) to allow deterministic unit testing 
 * without relying on real user interaction.
 */
class AppForTests {
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
    AppForTests();

    // Main loop: reads user commands and executes them
    void run();
};
