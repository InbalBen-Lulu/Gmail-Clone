#include "BloomStorage.h"
#include "../utils/FileUtils.h" // Import helper functions

#include <fstream>
#include <filesystem>
#include <vector>
#include <iostream>

const std::string BLOOM_FILE_PATH = "../data/bloom.txt";

// Constructor: initializes path and either calls init or loads existing data
BloomStorage::BloomStorage(bool newFile)
    : path(BLOOM_FILE_PATH), newFile(newFile) {
    if (newFile) {
        init();
    } else {
        load(); // for consistency, ensures file is accessible
    }
}

// Creates an empty file if newFile is true
void BloomStorage::init() {
    std::ofstream outFile = safeOpenOut(path, std::ios::trunc);
    outFile.close(); // Clear contents
}

// Loads file content into a vector of 0s and 1s
std::vector<int> BloomStorage::load() {
    std::vector<int> result;
    std::ifstream inFile = safeOpenIn(path);
    char ch;

    while (inFile.get(ch)) {
        if (ch == '0') result.push_back(0);
        else if (ch == '1') result.push_back(1);
    }

    return result;
}

// Updates the Bloom filter: ensures all 1s from input are reflected in file
void BloomStorage::update(const std::vector<int>& bitArray) {
    std::vector<int> current = load();

    // File was empty → initialize it
    if (current.empty()) {
        current = bitArray;
    } else {
        size_t maxSize = std::max(current.size(), bitArray.size());
        current.resize(maxSize, 0);

        for (size_t i = 0; i < bitArray.size(); ++i) {
            if (bitArray[i] == 1) {
                current[i] = 1;
            }
        }
    }

    std::ofstream outFile = safeOpenOut(path, std::ios::trunc);
    for (int bit : current) {
        outFile << (bit ? '1' : '0');
    }
    outFile.close();
}

// Returns whether a new file was initialized
bool BloomStorage::getNewFile() const {
    return newFile;
}
