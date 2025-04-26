#include "App.h"
#include <string>
#include <iostream>
#include "../io/ConsoleIOHandler.h"
#include "../commands/AddCommand.h"
#include "../commands/ContainCommand.h"
#include "../parser/InputParser.h"
#include "../storage/Params.h"

// Constructor: initializes the App with a ConsoleIOHandler (standard input/output)
App::App() {
    io = std::make_unique<ConsoleIOHandler>(std::cin, std::cout);
}

/*
 * Main execution loop of the application:
 * - Reads and parses the initialization input
 * - Initializes system components (storage, filter, commands)
 * - Continuously reads and executes user commands
 */
void App::run() {
    std::string line;
    size_t arraySize;
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

    // Main loop to read and execute user commands
    while (true) {
        std::string commandLine = io->readLine();
        std::optional<CommandInput> maybeCommand = InputParser::parseCommandLine(commandLine);
        if (!maybeCommand.has_value()) {
            continue;    // Skip invalid command lines
        }

        CommandInput cmd = maybeCommand.value();

        // Execute the parsed command (1 = Add, 2 = Contain)
        commands[cmd.commandId]->execute(cmd.url, *hash,*io);
    }
}

/*
 * Initializes system components:
 * - Sets up Params for hash array size and hash function configurations
 * - Creates storage managers for Bloom Filter and Blacklist
 * - Initializes Bloom Filter, Blacklist, and Hash instances
 * - Registers available commands in the commands map
 */
void App::initSystem(size_t arraySize, std::vector<int>& hashArray) {
    Params params(arraySize, hashArray);
    bool isNewFile = params.getNewFile();

    bloomStorage = std::make_unique<BloomStorage>(isNewFile);
    blackListStorage = std::make_unique<BlackListStorage>(isNewFile);

    bloomFilter = std::make_unique<BloomFilter>(*bloomStorage, arraySize);
    blackList = std::make_unique<BlackList>(*blackListStorage);
    hash = std::make_shared<Hash>(hashArray, arraySize);

    // Register available commands:
    // ID 1 -> AddCommand
    // ID 2 -> ContainCommand
    commands[1] = std::make_unique<AddCommand>(*bloomFilter, *blackList);
    commands[2] = std::make_unique<ContainCommand>(*bloomFilter, *blackList);    
}