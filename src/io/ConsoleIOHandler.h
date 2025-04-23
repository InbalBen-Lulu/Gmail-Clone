#pragma once
#include "IIOHandler.h"

class ConsoleIOHandler : public IIOHandler {
private:
    std::istream& in;
    std::ostream& out;
public:
    ConsoleIOHandler(int inFd, int outFd);
    std::string readLine() override;
    void writeLine(const std::string&) override;
};
