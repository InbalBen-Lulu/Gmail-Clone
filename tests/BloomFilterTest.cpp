#include <gtest/gtest.h>
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"

using namespace std;

TEST(BloomFilterTest, AddedHashResultsAreSet) {
    BloomStorage storage(true);
    BloomFilter filter(storage, 8);
    vector<int> hashResults = {1, 3};
    filter.add(hashResults);

    EXPECT_TRUE(filter.contain(hashResults));
}

TEST(BloomFilterTest, EmptyFilterReturnsFalse) {
    BloomStorage storage(true);
    BloomFilter filter(storage, 8);
    vector<int> hashResults = {2, 4};

    EXPECT_FALSE(filter.contain(hashResults));
}

