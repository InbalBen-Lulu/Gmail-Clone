#pragma once
#include <string>



class ICommand {
public:
    virtual void execute(const Url& url, Hash& hash, IIOHandler& io) = 0;
    virtual ~ICommand() = default;
};
