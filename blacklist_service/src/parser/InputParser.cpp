#include "InputParser.h"
#include <stdexcept>
#include <sstream>
#include <regex>

/*
 * Cleans an input string by removing extra spaces between words.
 * Returns the cleaned string.
 */
std::string InputParser::clean(const std::string& input) {
    std::istringstream iss(input);
    std::ostringstream oss;
    std::string word;
    bool first = true;
    while (iss >> word) {
        if (!first) {
            oss << ' ';    // add space between words
        }
        oss << word;
        first = false;
    }
    return oss.str();
}

/**
 * Parses server initialization arguments: port, array size, and hash repeat counts.
 * Expected format: ./server <port> <arraySize> <repeat1> <repeat2> ...
 * return true if all arguments are valid; false otherwise.
 */
bool InputParser::parseInitLine(int argc, char* argv[], int& port, size_t& arraySize, std::vector<int>& hashRepeats) {
    // Must have at least: program name + port + arraySize + one hash repeat
    if (argc < 4) {
        return false;
    }

    try {
        // Parse and validate port number
        port = std::stoi(argv[1]);
        if (port < 0 || port > 65535) {
            return false;   // Invalid port range
        }
        // Parse and validate array size
        int rawSize = std::stoi(argv[2]);
        if (rawSize <= 0) {
            return false;   // Array size must be positive
        }
        arraySize = static_cast<size_t>(rawSize);
        // Clear any previous values and parse hash repeat counts
        hashRepeats.clear();
        for (int i = 3; i < argc; ++i) {
            int count = std::stoi(argv[i]);
            if (count < 0) {
                return false;   // Hash repeat count must be non-negative
            }
            hashRepeats.push_back(count);
        }
        // Ensure at least one hash function was provided
        if (hashRepeats.empty()) {
            return false;     
        }

    } catch (const std::exception& e) {
        // Catch any conversion errors (invalid number format, etc.)
        return false;
    }
    return true;
}


/*
 * Validates if a given URL string matches a standard URL format.
 * Returns true if valid, false otherwise.
 */
bool InputParser::isValidUrl(const std::string& url) {
    static const std::regex urlRegex(
        R"(^(https?:\/\/)?([\w\-]+(\.[\w\-]+)+)(:[0-9]+)?(\/[\w\-._~:/?#[\]@!$&'()*+,;=]*)?$)",
        std::regex::icase
    );
    return std::regex_match(url, urlRegex);
}

/**
 * Parses a command line string into a valid CommandInput object if possible.
 * Returns std::nullopt on invalid format or unsupported command.
*/
std::optional<CommandInput> InputParser::parseCommandLine(const std::string& input) {
    std::istringstream iss(clean(input));
    std::string command;
    std::string urlStr;

    // Try to extract command and URL
    if (!(iss >> command) || !(iss >> urlStr)) {
        return std::nullopt; // missing command or URL
    }

    // Make sure there's no extra input
    std::string extra;
    if (iss >> extra) {
        return std::nullopt; // too many arguments
    }

    // Check that the command is one of the allowed types
    if (command != "POST" && command != "GET" && command != "DELETE") {
        return std::nullopt; // invalid command
    }

    // Validate the URL format
    if (!isValidUrl(urlStr)) {
        return std::nullopt; // invalid URL format
    }

    // All good — return parsed command
    return CommandInput{command, Url(urlStr)};
}