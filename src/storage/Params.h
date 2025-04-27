#pragma once
#include <string>
#include <vector>

// Handles parameters related to the Bloom filter configuration
class Params {
private:
    std::string path;
    bool newFile;
    size_t arraySize;
    std::vector<int> hashArray;

    // Build the parameter line to be saved into the file
    std::string buildParamLine() const;

    // Read the entire content of the parameter file
    std::string readFile() const;

    // Write content to the parameter file
    void writeFile(const std::string& content) const;
    
    // Check if the parameter file already exists
    bool fileExists() const;

public:
    // Constructor: initialize parameters and handle file creation or loading
    Params(size_t arraySize, const std::vector<int>& hashArray);

    // Return whether a new parameters file was initialized
    bool getNewFile() const;
};
