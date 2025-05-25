#include "Params.h"
#include "../utils/FileUtils.h" 

#include <fstream>
#include <sstream>
#include <filesystem>
#include <iostream>
#include <vector>

const std::string PARAMS_FILE_PATH = "data/params.txt";

// Constructor: checks if file exists and matches config, else creates new file
Params::Params(size_t arraySize, const std::vector<int>& hashArray)
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

// Creates a single-line string representing arraySize followed by hashArray
std::string Params::buildParamLine() const {
    std::ostringstream line;
    line << arraySize;
    for (int value : hashArray) {
        line << " " << value;
    }
    return line.str();
}

// Reads the first line of the parameter file
std::string Params::readFile() const {
    std::ifstream inFile = safeOpenIn(path);
    std::string line;
    std::getline(inFile, line);
    return line;
}

// Overwrites the parameter file with new content
void Params::writeFile(const std::string& content) const {
    std::ofstream outFile = safeOpenOut(path);
    outFile << content;
}

// Checks if the parameter file already exists
bool Params::fileExists() const {
    return std::filesystem::exists(path);
}

// Returns whether a new file was written
bool Params::getNewFile() const {
    return newFile;
}
