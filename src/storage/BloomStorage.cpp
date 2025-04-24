#include "BloomStorage.h"
#include <fstream>
#include <filesystem>
#include <vector>
#include <iostream>

const std::string BLOOM_FILE_PATH = "../data/bloomfilter.txt";

// --- Constructor ---
BloomStorage::BloomStorage(bool newFile)
    : path(BLOOM_FILE_PATH), newFile(newFile) {
    if (newFile) {
        init();
    }
}

// --- init ---
// Creates an empty file (no content)
void BloomStorage::init() {
    std::ofstream outFile(path, std::ios::trunc);  // Truncate to empty
    outFile.close();
}

// --- load ---
// Reads file content and returns vector<int> of 0s and 1s
std::vector<int> BloomStorage::load() {
    std::vector<int> result;
    std::ifstream inFile(path);
    char ch;

    while (inFile.get(ch)) {
        if (ch == '0') result.push_back(0);
        else if (ch == '1') result.push_back(1);
    }

    return result;
}

// --- update ---
// Ensures all 1s in input are reflected in file; modifies only if needed
void BloomStorage::update(const std::vector<int>& bitArray) {
    std::vector<int> current = load();

    // If file is empty → create it from scratch
    if (current.empty()) {
        current = bitArray;
    } else {
        // Otherwise, ensure all '1's from bitArray are also '1' in current
        size_t maxSize = std::max(current.size(), bitArray.size());
        current.resize(maxSize, 0);

        for (size_t i = 0; i < bitArray.size(); ++i) {
            if (bitArray[i] == 1) {
                current[i] = 1;
            }
        }
    }

    // Write back the updated content
    std::ofstream outFile(path, std::ios::trunc);
    for (int bit : current) {
        outFile << (bit ? '1' : '0');
    }
    outFile.close();
}

// --- getNewFile ---
bool BloomStorage::getNewFile() const {
    return newFile;
}
