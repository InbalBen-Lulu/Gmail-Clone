#include <fstream>
#include <filesystem>
#include <set>
#include "BlackListStorage.h"
#include "../utils/Url.h"
#include "../utils/FileUtils.h"

const std::string BlackList_FILE_PATH = "../data/BlackList.txt";

// Receives a flag whether to initialize a new file or not
BlackListStorage::BlackListStorage(bool newFile)
    : path(BlackList_FILE_PATH), newFile(newFile) {}

// Creates or clears the file if newFile is true
void BlackListStorage::init() {
    if (newFile) {
        std::ofstream outFile = safeOpenOut(path, std::ios::trunc); // use helper for safety
        outFile.close(); // optional but clean
    }
}

// Appends a URL to the file (one per line)
void BlackListStorage::add(const Url& url) {
    std::ofstream outFile = safeOpenOut(path, std::ios::app); // use helper
    outFile << url.getUrlPath() << "\n";
    outFile.close();
}

// Loads all URLs from the file into a set
std::set<Url> BlackListStorage::load() {
    std::set<Url> result;
    std::ifstream inFile = safeOpenIn(path); // use helper

    std::string line;
    while (std::getline(inFile, line)) {
        Url url(line);
        result.insert(url);
    }

    return result;
}

bool BlackListStorage::getNewFile() const {
    return newFile;
}
