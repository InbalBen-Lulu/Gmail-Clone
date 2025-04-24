#include "BlacklistStorage.h"
#include <fstream>
#include <filesystem>
#include "../utils/Url.h"

const std::string BLACKLIST_FILE_PATH = "../data/blacklist.txt";

// --- Constructor ---
// Receives a flag whether to initialize a new file or not
BlacklistStorage::BlacklistStorage(bool newFile)
    : path(BLACKLIST_FILE_PATH), newFile(newFile) {}

// --- init ---
// Creates or clears the file if newFile is true
void BlacklistStorage::init() {
    if (newFile) {
        std::ofstream outFile(path);  // This will truncate the file if it exists
        outFile.close();
    }
}

// --- add ---
// Appends a URL to the file (one per line)
void BlacklistStorage::add(const Url& url) {
    std::ofstream outFile(path, std::ios::app);  // Open in append mode
    if (outFile.is_open()) {
        outFile << url.getUrlPath() << "\n";
        outFile.close();
    }
}

// --- load ---
// Loads all URLs from the file into a set
std::set<Url> BlacklistStorage::load() {
    std::set<Url> result;
    std::ifstream inFile(path);
    std::string line;

    while (std::getline(inFile, line)) {
        Url url(line);
        if (url.isValid()) {
            result.insert(url);
        }
    }

    return result;
}

// --- getNewFile ---
bool BlacklistStorage::getNewFile() const {
    return newFile;
}
