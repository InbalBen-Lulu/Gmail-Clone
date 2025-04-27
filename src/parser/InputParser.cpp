#include "InputParser.h"
#include <sstream>
#include <cctype>
#include <algorithm>
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

/*
 * Parses the initialization line, extracting the array size and hash configuration.
 * Returns true if:
 * - All characters are digits or spaces
 * - Array size > 0
 * - Each hash function ID is a positive integer
 * - At least one hash function is provided
 */
bool InputParser::parseInitLine(const std::string& line, size_t& arraySize, std::vector<int>& hashConfig) {
    std::string cleaned = clean(line);

    // Ensure all characters are digits or spaces
    for (char c : cleaned) {
        if (!std::isdigit(c) && !std::isspace(c)) {
            return false;
        }
    }
    
    // Parse the cleaned line
    std::istringstream iss(cleaned);

    size_t size;
    if (!(iss >> size) || size == 0) {
        return false;   // Invalid or zero array size
    }

    arraySize = size;
    hashConfig.clear();

    int val;
    while (iss >> val) {
        if (val <= 0) {
            return false;    
        }
        hashConfig.push_back(val);
    }

    // Ensure at least one hash function was provided
    return !hashConfig.empty();
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

/*
 * Parses a command line into a CommandInput struct containing a command ID and URL.
 * Returns std::nullopt if parsing fails.
 */
std::optional<CommandInput> InputParser::parseCommandLine(const std::string& input) {
    std::istringstream iss(clean(input));
    int commandId;
    std::string url;

    if (!(iss >> commandId) || !(iss >> url)) {
        return std::nullopt;   // missing command or URL
    }

    std::string extra;
    if (iss >> extra) {
        return std::nullopt;  // extra tokens detected
    }

    if (commandId != 1 && commandId != 2) {
        return std::nullopt;    // invalid command ID
    }

    if (!isValidUrl(url)) {
        return std::nullopt;   // invalid URL
    }

    return CommandInput{commandId, Url(url)};
}
