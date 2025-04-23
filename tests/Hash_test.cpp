#include <gtest/gtest.h>
#include "Url.h"
#include <memory>
#include <vector>
#include <string>
#include "../src/utils/Hash.h"


// ===================== HASH::EXECUTE TEST =====================

TEST(HashTest, ExecuteGeneratesCorrectHashes) {
    // Hash config: two functions — one with 1x std::hash, one with 2x
    auto array = std::make_unique<int[]>(2);
    array[0] = 1;  // first function: 1 hash
    array[1] = 2;  // second function: 2 hashes

    int arraySize = 8;
    Hash hash(std::move(array), arraySize);
    Url url("www.example.com0");

    std::vector<int> results = hash.execute(url);

    // Should return 2 hash values
    ASSERT_EQ(results.size(), 2);

    std::hash<std::string> hasher;
    size_t h1 = hasher(url.getUrlPath());
    size_t h2 = hasher(std::to_string(h1));

    int expected1 = static_cast<int>(h1 % arraySize);
    int expected2 = static_cast<int>(h2 % arraySize);

    EXPECT_EQ(results[0], expected1);
    EXPECT_EQ(results[1], expected2);
}

TEST(HashTest, OneHashFunctionTwiceShouldReturnOneResult) {
    auto array = std::make_unique<int[]>(2);
    array[0] = 1;
    array[1] = 1;

    int arraySize = 8;
    Hash hash(std::move(array), arraySize);
    Url url("www.example.com");

    std::vector<int> results = hash.execute(url);

    // Two hash functions that each hash once — expect two values
    ASSERT_EQ(results.size(), 2);

    std::hash<std::string> hasher;
    size_t h1 = hasher(url.getUrlPath());
    size_t h2 = hasher(url.getUrlPath());

    int expected1 = static_cast<int>(h1 % arraySize);
    int expected2 = static_cast<int>(h2 % arraySize);

    EXPECT_EQ(results[0], expected1);
    EXPECT_EQ(results[1], expected2);
}

TEST(HashTest, MoreThanTwoHashFunctionsReturnsCorrectCount) {
    auto array = std::make_unique<int[]>(5);
    array[0] = 1; 
    array[1] = 2;  
    array[2] = 1;  
    array[3] = 3; 
    array[4] = 1;  

    int arraySize = 16;
    Hash hash(std::move(array), arraySize);
    Url url("test.com");

    std::vector<int> results = hash.execute(url);

    // We should still get 5 results, even if some values are duplicates
    ASSERT_EQ(results.size(), 5);

    for (int val : results) {
        EXPECT_GE(val, 0);
        EXPECT_LT(val, arraySize);
    }

    // Optional: check if repeated hashes return same value
    EXPECT_EQ(results[0], results[2]);
    EXPECT_EQ(results[0], results[4]);
}
