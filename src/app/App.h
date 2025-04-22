#pragma once
#include <memory>
#include <map>
#include <string>
#include "IIOHandler.h"
#include "BloomFilter.h"
#include "Blacklist.h"
#include "ICommand.h"
#include "Hash.h"

class App {
private:
    std::unique_ptr<IIOHandler> io;
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<Blacklist> blacklist;
    std::map<int, std::unique_ptr<ICommand>> commands;
    Hash hash;
public:
    App();
    void init();
    void run();
};
