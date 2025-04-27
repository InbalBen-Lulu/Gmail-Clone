#pragma once
#include <string>

/*
 * IIOHandler interface:
 * Defines an abstract interface for handling input and output operations.
 * Can be implemented for different I/O sources like console, files, network, etc.
 */
class IIOHandler {
public:

    /*
     * Reads a single line from the input source.
     * Returns the read line as a string.
     */
    virtual std::string readLine() = 0;

    /*
     * Writes a single line to the output destination.
     * Takes the line as a constant string reference.
     */
    virtual void writeLine(const std::string&) = 0;

    // Virtual destructor to ensure proper cleanup through interface pointers
    virtual ~IIOHandler() = default;
};

