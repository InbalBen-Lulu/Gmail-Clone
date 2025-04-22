#pragma once
#include <string>
class Hash;

class ICommand {
public:
    virtual void execute(const std::string& url, Hash& hash) = 0;
    virtual ~ICommand() = default;
};
