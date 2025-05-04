#pragma once
#include <string>
#include <vector>
#include <optional>
#include "../utils/Url.h"

// Struct representing a parsed command input: command ID and URL
struct CommandInput {
    string command;
    Url url;
};

/*
 * InputParser class:
 * Provides static utility methods to parse and validate user input.
 * Supports cleaning input, parsing initialization lines, parsing commands, and validating URLs.
 */
class InputParser {
public:
    // Removes extra spaces from the input string
    static std::string clean(const std::string& str);

    static bool parseInitLine(int argc, char* argv[], int& port, size_t& arraySize, std::vector<int>& hashRepeats);
    
    // Parses a command line into a CommandInput object
    static std::optional<CommandInput> parseCommandLine(const std::string& str);
    
    // Validates if the given string is a valid URL
    static bool isValidUrl(const std::string& url);
};
