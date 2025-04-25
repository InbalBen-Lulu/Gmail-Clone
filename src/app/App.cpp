#include "App.h"
#include <iostream>
#include "../io/ConsoleIOHandler.h"
#include "../commands/AddCommand.h"
#include "../commands/ContainCommand.h"
#include "../parser/InputParser.h"
#include "../storage/Params.h"

App::App() {
    io = std::make_unique<ConsoleIOHandler>(std::cin, std::cout);
}

void App::run() {
    std::string line;
    int arraySize;
    std::vector<int> hashArray;

    while (true) {
        line = io->readLine();
        if (InputParser::parseInitLine(line, arraySize, hashArray)) {
            break;
        }
    }

    initSystem(arraySize, hashArray);

    while (true) {
        std::string commandLine = io->readLine();
        std::optional<CommandInput> maybeCommand = InputParser::parseCommandLine(commandLine);
        if (!maybeCommand.has_value()) {
            continue; 
        }
        CommandInput cmd = maybeCommand.value();

        commands[cmd.commandId]->execute(cmd.url, *hash,*io);
    }
}

void App::initSystem(int arraySize, const std::vector<int>& hashArray) {
    Params params(arraySize, hashArray);
    bool isNewFile = params.getNewFile();

    bloomStorage = std::make_unique<BloomStorage>(isNewFile);
    blackListStorage = std::make_unique<BlackListStorage>(isNewFile);

    bloomFilter = std::make_unique<BloomFilter>(*bloomStorage, arraySize);
    blackList = std::make_unique<BlackList>(*blackListStorage);
    hash = std::make_shared<Hash>(hashArray, arraySize);

    commands[1] = std::make_unique<AddCommand>(*bloomFilter, *blackList, *io);
    commands[2] = std::make_unique<ContainCommand>(*bloomFilter, *blackList, *io);    
}