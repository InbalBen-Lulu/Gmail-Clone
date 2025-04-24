#include "ConsoleIOHandler.h"
#include <string>
#include <istream>
#include <ostream>

ConsoleIOHandler::ConsoleIOHandler(std::istream& inStream, std::ostream& outStream)
    : in(inStream), out(outStream) {}

std::string ConsoleIOHandler::readLine() {
    std::string line;
    if (std::getline(in, line)) {
        return line;
    }
    return "";
}

void ConsoleIOHandler::writeLine(const std::string& line) {
    out << line << '\n';
}