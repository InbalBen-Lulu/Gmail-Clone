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

bool InputParser::parseInitLine(int argc, char* argv[], int& port, size_t& arraySize, std::vector<int>& hashRepeats) {
    std::vector<std::string> args;

    // Skip empty argv entries
    for (int i = 1; i < argc; ++i) {
        std::string arg(argv[i]);
        if (!arg.empty()) {
            args.push_back(arg);
        }
    }

    // Must have at least port + array size + one hash function
    if (args.size() < 3) {
        return false;
    }

    try {
        // Parse and validate port number
        port = std::stoi(args[0]);
        if (port < 1024 || port > 65535) {
            return false;  // unsafe or invalid port
        }

        // Parse and validate array size
        int rawSize = std::stoi(args[1]);
        if (rawSize <= 0) {
            return false;
        }
        arraySize = static_cast<size_t>(rawSize);

        // Parse hash function repeat counts
        hashRepeats.clear();
        for (size_t i = 2; i < args.size(); ++i) {
            int count = std::stoi(args[i]);
            if (count < 0) {
                return false;
            }
            hashRepeats.push_back(count);
        }

        if (hashRepeats.empty()) {
            return false; // no hash functions given
        }

    } catch (const std::exception&) {
        // Catch parsing errors like invalid stoi input
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
