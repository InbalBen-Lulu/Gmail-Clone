#pragma once
#include <string>

class IIOHandler {
public:
    virtual std::string readLine() = 0;
    virtual void writeLine(const std::string&) = 0;
    virtual ~IIOHandler() = default;
};

