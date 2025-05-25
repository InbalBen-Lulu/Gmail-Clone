#include <gtest/gtest.h>
#include <memory>
#include <vector>
#include <string>
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"

TEST(HashTest, ExecuteGeneratesCorrectBitVector) {
    std::vector<int> config = {1, 2};  
    size_t arraySize = 8;

    Hash hash(config, arraySize);
    Url url("www.example.com0");

    std::vector<int> result = hash.execute(url);

    ASSERT_EQ(result.size(), arraySize);

    std::hash<std::string> hasher;
    size_t h1 = hasher(url.getUrlPath());
    size_t h2 = hasher(std::to_string(h1));

    int idx1 = static_cast<int>(h1 % arraySize);
    int idx2 = static_cast<int>(h2 % arraySize);

    // ensure both indices are marked with 1
    EXPECT_EQ(result[idx1], 1);
    EXPECT_EQ(result[idx2], 1);

    // all other indices must be 0 (unless idx1 == idx2)
    for (int i = 0; i < arraySize; ++i) {
        if (i != idx1 && i != idx2)
            EXPECT_EQ(result[i], 0);
    }
}

TEST(HashTest, OneHashFunctionTwiceShouldMarkSameIndex) {
    std::vector<int> config = {1, 1};
    size_t arraySize = 8;

    Hash hash(config, arraySize);
    Url url("www.example.com");

    std::vector<int> result = hash.execute(url);
    ASSERT_EQ(result.size(), arraySize);

    std::hash<std::string> hasher;
    size_t h1 = hasher(url.getUrlPath());
    int idx = static_cast<int>(h1 % arraySize);

    EXPECT_EQ(result[idx], 1);

    for (int i = 0; i < arraySize; ++i) {
        if (i != idx)
            EXPECT_EQ(result[i], 0);
    }
}

TEST(HashTest, RepeatedIdenticalHashFunctionsOnlySetSingleBit) {
    std::vector<int> config = {1, 1, 1}; // 3 times same hash function
    size_t arraySize = 16;

    Hash hash(config, arraySize);
    Url url("test.com");

    std::vector<int> result = hash.execute(url);
    ASSERT_EQ(result.size(), arraySize);

    int countOnes = 0;
    for (int bit : result) {
        if (bit == 1) ++countOnes;
        else EXPECT_EQ(bit, 0);
    }

    EXPECT_EQ(countOnes, 1); // All 3 functions return same index
}
