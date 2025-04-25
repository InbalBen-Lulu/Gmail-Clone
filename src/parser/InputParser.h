#pragma once
#include <string>
#include <vector>
#include <optional>

struct CommandInput {
    int commandId;
    std::string url;
};

class InputParser {
public:
    static std::string clean(const std::string&);
    static bool parseInitLine(const std::string&, size_t&, std::vector<int>&);
    static std::optional<CommandInput> parseCommandLine(const std::string&);
    static bool isValidUrl(const std::string& url);

};
