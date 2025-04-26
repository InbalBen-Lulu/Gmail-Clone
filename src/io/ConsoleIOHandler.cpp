#include "ConsoleIOHandler.h"
#include <string>
#include <istream>
#include <ostream>

ConsoleIOHandler::ConsoleIOHandler(std::istream& inStream, std::ostream& outStream)
    : in(inStream), out(outStream) {}

// Read a line from input stream
std::string ConsoleIOHandler::readLine() {
    std::string line;
    if (std::getline(in, line)) {
        return line;
    }
    return "";
}

// Write a line to output stream
void ConsoleIOHandler::writeLine(const std::string& line) {
    out << line << '\n';
}