#include "Server.h"
#include "../parser/InputParser.h"
#include <iostream>

/**
 * Entry point of the server application.
 * Expects command-line arguments to configure port, bloom filter size, and hash configuration.
 */
int main(int argc, char* argv[]) {
    int port;
    size_t arraySize;
    std::vector<int> hashArray;

    // Parse the command-line arguments and populate port, arraySize, and hashArray
    if (!InputParser::parseInitLine(argc, argv, port, arraySize, hashArray)) {
        return 1;
    }

    // Create the server instance with the parsed configuration
    Server server(port, arraySize, hashArray);
    // Start the server: set up socket and handle one client
    server.run();
    return 0;
}