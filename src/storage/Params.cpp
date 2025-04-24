#include <fstream>
#include <sstream>
#include <filesystem>
#include "Params.h"

// Path to the parameters file
const std::string PARAMS_FILE_PATH = "../../data/params.txt";

// --- Constructor ---
Params::Params(int arraySize, int* hashArray)
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

// Creates a single-line string representing the parameters
std::string Params::buildParamLine() const {
    std::ostringstream line;
    line << arraySize;
    for (int i = 0; i < arraySize; ++i) {
        line << " " << hashArray[i];
    }
    return line.str();
}

// Reads the first line from the parameters file
std::string Params::readFile() const {
    std::ifstream inFile(path);
    std::string line;
    std::getline(inFile, line);
    return line;
}

// Overwrites the parameters file with the given content
void Params::writeFile(const std::string& content) const {
    std::ofstream outFile(path);
    outFile << content;
}

// Checks if the parameters file already exists
bool Params::fileExists() const {
    return std::filesystem::exists(path);
}

// Indicates whether the file was newly written
bool Params::getNewFile() const {
    return newFile;
}
