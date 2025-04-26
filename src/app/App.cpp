#include "App.h"
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
    int iter = 0;
    std::string line;
    size_t arraySize;
    std::vector<int> hashArray;
    bool check = false;

    // Loop until a valid initialization line is provided
    while (!check && iter < 4) {
        line = io->readLine();
        // std::cout << "[DEBUG] Read line: " << line << std::endl;
        // std::cout.flush();

        iter++;
        check = InputParser::parseInitLine(line, arraySize, hashArray);
        if (check) {
            // std::cout << "[DEBUG] Init line parsed successfully." << std::endl;
            // std::cout.flush();

            break;
        } else {
            // std::cout << "[DEBUG] Init line parsing failed." << std::endl;
            // std::cout.flush();
        }   
    }

    if (!check) {
        // std::cout << "[DEBUG] Failed to parse a valid init line after 4 reads. Exiting App::run." << std::endl;
        // std::cout.flush();
        return;  
    }    

    // Initialize system components based on parsed parameters
    initSystem(arraySize, hashArray);

    // Main loop to read and execute user commands
    while (iter < 4) {
        std::string commandLine = io->readLine();
        // std::cout << "[DEBUG] Read line: " << commandLine << std::endl;
        // std::cout.flush();

        iter++;
        std::optional<CommandInput> maybeCommand = InputParser::parseCommandLine(commandLine);
        if (!maybeCommand.has_value()) {
            // std::cout << "[DEBUG] Invalid command line: " << commandLine << std::endl;
            // std::cout.flush();

            continue;    // Skip invalid command lines
        }
        CommandInput cmd = maybeCommand.value();
        // std::cout << "[DEBUG] Executing command ID: " << cmd.commandId << " with URL: " << cmd.url.getUrlPath() << std::endl;
        // std::cout.flush();

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