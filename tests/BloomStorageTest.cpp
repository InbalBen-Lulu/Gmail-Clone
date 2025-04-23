#include <gtest/gtest.h>
#include <fstream>
#include <cstdio>
#include "BloomStorage.h"

using namespace std;

// Test: update() should write the bit array correctly,
// and load() should return the same array
TEST(BloomStorageTest, UpdateAndLoadBitArray) {
    remove("bloom.txt");

    BloomStorage storage(true);
    vector<int> expectedBits = {1, 0, 1, 1, 0, 0, 1, 1};
    storage.update(expectedBits);

    vector<int> loadedBits = storage.load();
    EXPECT_EQ(loadedBits, expectedBits);
}

TEST(BloomStorageTest, UpdateWritesBitArrayToFile) {
    remove("bloom.txt");

    BloomStorage storage(true);
    vector<int> bitArray = {1, 0, 1, 1, 0, 0, 1, 1};
    storage.update(bitArray);

    ifstream file("bloom.txt");
    ASSERT_TRUE(file.is_open());

    string line;
    getline(file, line);
    file.close();

    string expected = "10110011";
    EXPECT_EQ(line, expected);
}

TEST(BloomStorageTest, DataIsPersistentAcrossInstances) {
    remove("bloom.txt");

    // Create first instance and write data
    vector<int> originalBits = {1, 0, 1, 1, 0, 1, 0, 0};
    BloomStorage storage1(true); // simulate "new file"
    storage1.update(originalBits);

    // Create second instance and load the data
    BloomStorage storage2(false); 
    vector<int> loadedBits = storage2.load();

    EXPECT_EQ(loadedBits, originalBits); // check if data was preserved exactly
}