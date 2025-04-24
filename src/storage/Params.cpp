#include <fstream>
#include <sstream>
#include <filesystem>
#include <iostream>
#include <vector>
#include "Params.h"

const std::string PARAMS_FILE_PATH = "../data/params.txt";

// --- Constructor ---
Params::Params(int arraySize, const std::vector<int>& hashArray)
    : path(PARAMS_FILE_PATH), arraySize(arraySize), hashArray(hashArray) {

    std::string currentContent = buildParamLine();

    if (fileExists()) {
        std::string existingContent = readFile();

        if (existingContent == currentContent) {
            newFile = false;
            return;
        }
    }

    writeFile(currentContent);
    newFile = true;
}

// --- buildParamLine ---
std::string Params::buildParamLine() const {
    std::ostringstream line;
    line << arraySize;
    for (int value : hashArray) {
        line << " " << value;
    }
    return line.str();
}

std::string Params::readFile() const {
    std::ifstream inFile(path);
    std::string line;
    std::getline(inFile, line);
    return line;
}

void Params::writeFile(const std::string& content) const {
    std::ofstream outFile(path);
    outFile << content;
}

bool Params::fileExists() const {
    return std::filesystem::exists(path);
}

bool Params::getNewFile() const {
    return newFile;
}
