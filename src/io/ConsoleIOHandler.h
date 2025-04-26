#pragma once
#include "IIOHandler.h"

/*
 * ConsoleIOHandler class:
 * Implements the IIOHandler interface using standard console input and output.
 * Reads lines from std::cin and writes lines to std::cout.
 */
class ConsoleIOHandler : public IIOHandler {
private:
    std::istream& in;
    std::ostream& out;

public:
    // Constructor: initializes ConsoleIOHandler with input and output streams
    ConsoleIOHandler(std::istream& in, std::ostream& out);

    // Reads a line from the input stream
    std::string readLine() override;

    // Writes a line to the output stream
    void writeLine(const std::string&) override;
};
