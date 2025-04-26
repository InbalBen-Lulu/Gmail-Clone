#include "App.h"
#include <iostream>
#include "../io/ConsoleIOHandler.h"
#include "../commands/AddCommand.h"
#include "../commands/ContainCommand.h"
#include "../parser/InputParser.h"
#include "../storage/Params.h"

// Initialize console I/O handler
App::App() {
    io = std::make_unique<ConsoleIOHandler>(std::cin, std::cout);
}

// Main application loop
void App::run() {
    std::string line;
    int arraySize;
    std::vector<int> hashArray;

    // Loop until a valid initialization line is provided
    while (true) {
        line = io->readLine();
        if (InputParser::parseInitLine(line, arraySize, hashArray)) {
            break;
        }
    }

    // Initialize system components based on parsed parameters
    initSystem(arraySize, hashArray);

    // Command execution loop
    while (true) {
        std::string commandLine = io->readLine();
        std::optional<CommandInput> maybeCommand = InputParser::parseCommandLine(commandLine);
        if (!maybeCommand.has_value()) {
            continue;   // Invalid input, read next line
        }
        CommandInput cmd = maybeCommand.value();

        // Execute command (1 = add, 2 = contain)
        commands[cmd.commandId]->execute(cmd.url, *hash,*io);
    }
}

// Initialize system components (storage, filter, commands)
void App::initSystem(int arraySize, std::vector<int>& hashArray) {
    Params params(arraySize, hashArray);
    bool isNewFile = params.getNewFile();

    bloomStorage = std::make_unique<BloomStorage>(isNewFile);
    blackListStorage = std::make_unique<BlackListStorage>(isNewFile);

    bloomFilter = std::make_unique<BloomFilter>(*bloomStorage, arraySize);
    blackList = std::make_unique<BlackList>(*blackListStorage);
    hash = std::make_shared<Hash>(hashArray, arraySize);

    // Register commands by ID
    commands[1] = std::make_unique<AddCommand>(*bloomFilter, *blackList);
    commands[2] = std::make_unique<ContainCommand>(*bloomFilter, *blackList);    
}