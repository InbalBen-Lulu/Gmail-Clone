#include "ConsoleIOHandler.h"
#include <string>
#include <istream>
#include <ostream>

// Constructor: initializes ConsoleIOHandler with input and output streams
ConsoleIOHandler::ConsoleIOHandler(std::istream& inStream, std::ostream& outStream)
    : in(inStream), out(outStream) {}

/*
 * Reads a single line from the input stream.
 * Returns the line as a string, or an empty string if reading fails.
 */
std::string ConsoleIOHandler::readLine() {
    std::string line;
    if (std::getline(in, line)) {
        return line;
    }
    return "";
}

/*
 * Writes a given string line to the output stream, followed by a newline character.
 */
void ConsoleIOHandler::writeLine(const std::string& line) {
    out << line << '\n';
    out.flush();
}