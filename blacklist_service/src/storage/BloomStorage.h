#pragma once
#include <string>
#include <vector>

// Handles persistent storage of the Bloom filter bit array
class BloomStorage {
private:
    std::string path;
    bool newFile;
public:
    // Constructor: initialize storage with an option to create a new file
    BloomStorage(bool newFile);

    // Update the Bloom filter file with the provided bit array
    void update(const std::vector<int>& bitArray);

    // Load the Bloom filter content from the file into a bit array
    std::vector<int> load();

    // Return whether a new file was initialized
    bool getNewFile() const;

    // Initialize the Bloom filter file (create or clear contents)
    void init();
};
