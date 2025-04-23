#pragma once
#include <memory>
#include <map>
#include <string>
#include "IIOHandler.h"
#include "BloomFilter.h"
#include "BlackList.h"
#include "ICommand.h"
#include "Hash.h"

class App {
private:
    std::unique_ptr<IIOHandler> io;
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<BlackList> blackList;
    std::map<int, std::unique_ptr<ICommand>> commands;
    std::unique_ptr<Hash>;
public:
    App();
    void run();
};
