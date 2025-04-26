#pragma once
#include "IIOHandler.h"

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
