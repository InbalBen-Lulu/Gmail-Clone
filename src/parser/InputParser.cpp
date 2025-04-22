#include "InputParser.h"
#include <sstream>

std::string InputParser::clean(const std::string& input) {
    return input; // TODO: Clean input (trim, sanitize, etc.)
}

bool InputParser::parseInitLine(const std::string& line, size_t& size, std::vector<int>& config) {
    // TODO: Parse initialization line (e.g., from params.txt)
    return false;
}

std::optional<CommandInput> InputParser::parseCommandLine(const std::string& line) {
    std::istringstream iss(line);
    CommandInput input;
    if (iss >> input.commandId >> input.url) {
        return input;
    }
    return std::nullopt;
}
