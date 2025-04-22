#include "ConsoleIOHandler.h"
#include <iostream>

ConsoleIOHandler::ConsoleIOHandler(int inFd, int outFd)
    : inputFd(inFd), outputFd(outFd) {}

std::string ConsoleIOHandler::readLine() {
    std::string line;
    std::getline(std::cin, line);
    return line;
}

void ConsoleIOHandler::writeLine(const std::string& message) {
    std::cout << message << std::endl;
}
