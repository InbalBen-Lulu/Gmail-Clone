#pragma once

#include <fstream>
#include <string>
#include <stdexcept>

// Opens a file for writing. Throws if the file cannot be opened.
// path: Path to the file to open
// mode: File open mode (default is std::ios::out)
// Returns: An open std::ofstream object
inline std::ofstream safeOpenOut(const std::string& path, std::ios::openmode mode = std::ios::out) {
    std::ofstream outFile(path, mode);
    if (!outFile.is_open()) {
        throw std::runtime_error("Failed to open output file: " + path);
    }
    return outFile;
}

// Opens a file for reading. Throws if the file cannot be opened.
// path: Path to the file to open
// Returns: An open std::ifstream object
inline std::ifstream safeOpenIn(const std::string& path) {
    std::ifstream inFile(path);
    if (!inFile.is_open()) {
        throw std::runtime_error("Failed to open input file: " + path);
    }
    return inFile;
}
