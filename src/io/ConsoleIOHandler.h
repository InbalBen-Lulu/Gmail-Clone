#pragma once
#include "IIOHandler.h"

class ConsoleIOHandler : public IIOHandler {
private:
    int inputFd;
    int outputFd;
public:
    ConsoleIOHandler(int inFd, int outFd);
    std::string readLine() override;
    void writeLine(const std::string&) override;
};
