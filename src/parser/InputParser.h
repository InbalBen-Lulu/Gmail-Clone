#pragma once
#include <string>
#include <vector>
#include <optional>
#include "../utils/Url.h"

struct CommandInput {
    int commandId;
    Url url;
};

class InputParser {
public:
    static std::string clean(const std::string&);
    static bool parseInitLine(const std::string&, int&, std::vector<int>&);
    static std::optional<CommandInput> parseCommandLine(const std::string&);
    static bool isValidUrl(const std::string& url);
};
