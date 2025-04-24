#include "InputParser.h"
#include <sstream>
#include <cctype>
#include <algorithm>

std::string InputParser::clean(const std::string& input) {
    std::istringstream iss(input);
    std::ostringstream oss;
    std::string word;
    bool first = true;
    while (iss >> word) {
        if (!first) {
            oss << ' ';
        }
        oss << word;
        first = false;
    }
    return oss.str();
}

bool InputParser::parseInitLine(const std::string& line, size_t& arraySize, std::vector<int>& hashConfig) {
    std::istringstream iss(clean(line));
    int size;
    if (!(iss >> size) || size <= 0) {
        return false;
    }

    arraySize = static_cast<size_t>(size);
    hashConfig.clear();

    int val;
    while (iss >> val) {
        if (val < 0) {
            return false;
        }
        hashConfig.push_back(val);
    }
    
    // Require at least one hash function after the array size
    return !hashConfig.empty();
}

std::optional<CommandInput> InputParser::parseCommandLine(const std::string& input) {
    std::istringstream iss(clean(input));
    int commandId;
    std::string url;

    if (!(iss >> commandId) || !(iss >> url)) {
        return std::nullopt;
    }

    std::string extra;
    if (iss >> extra) {
        return std::nullopt;  
    }

    if (commandId != 1 && commandId != 2) {
        return std::nullopt;
    }

    return CommandInput{commandId, url};
}
