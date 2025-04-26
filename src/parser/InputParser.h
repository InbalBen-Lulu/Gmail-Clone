#pragma once
#include <string>
#include <vector>
#include <optional>
#include "../utils/Url.h"

// Struct representing a parsed command input: command ID and URL
struct CommandInput {
    int commandId;
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
    static std::string clean(const std::string&);

    // Parses the initialization line to extract array size and hash configuration
    static bool parseInitLine(const std::string&, size_t&, std::vector<int>&);
    
    // Parses a command line into a CommandInput object
    static std::optional<CommandInput> parseCommandLine(const std::string&);
    
    // Validates if the given string is a valid URL
    static bool isValidUrl(const std::string& url);
};
