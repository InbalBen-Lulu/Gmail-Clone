#pragma once
#include <string>
#include "../data/BloomFilter.h"
#include "../data/BlackList.h"
#include "../utils/Hash.h"
#include "../utils/Url.h"
#include "../io/IIOHandler.h"

class ICommand {
public:
    virtual void execute(const Url& url, Hash& hash, IIOHandler& io) = 0;
    virtual ~ICommand() = default;
};
