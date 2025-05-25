#include <gtest/gtest.h>
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"

using namespace std;

TEST(BloomFilterTest, AddedHashResultsAreSet) {
    remove("bloom.txt");  // ensure clean start

    BloomStorage storage(true);
    BloomFilter filter(storage, 8);

    vector<int> hashResults = {0, 1, 0, 1, 1, 0, 0, 0};
    filter.add(hashResults);

    // Check with original filter
    EXPECT_TRUE(filter.contain(hashResults));

    // Now reload with new instance (persistence test)
    BloomStorage storage2(false);
    BloomFilter filter2(storage2, 8);
    EXPECT_TRUE(filter2.contain(hashResults));
}

TEST(BloomFilterTest, EmptyFilterReturnsFalse) {
    BloomStorage storage(true);
    BloomFilter filter(storage, 8);
    vector<int> hashResults = {0, 1, 0, 1, 1, 0, 1, 0};

    EXPECT_FALSE(filter.contain(hashResults));
}

