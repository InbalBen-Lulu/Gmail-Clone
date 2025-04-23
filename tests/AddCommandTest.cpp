#include <gtest/gtest.h>
#include <memory>
#include <cstdio>
#include "AddCommand.h"
#include "BloomFilter.h"
#include "BlackList.h"
#include "BloomStorage.h"
#include "BlackListStorage.h"
#include "Hash.h"
#include "Url.h"

using namespace std;

TEST(AddCommandTest, ExecuteAddsToBlackListAndBloomFilter) {
    remove("bloom.txt"); // start with clean file

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);

    AddCommand addCommand(bloomFilter, blackList);

    Url url("www.check.com");
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

    addCommand.execute(url, hash);

    // Check if URL is in the blacklist
    EXPECT_TRUE(blackList.contains(url));
}


TEST(AddCommandTest, ExecuteAddsToBloomFilter) {
    remove("bloom.txt"); // start with clean file

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);
    AddCommand addCommand(bloomFilter, blackList);

    Url url("www.check.com");
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

    addCommand.execute(url, hash);

    // Check if corresponding bits are set in BloomFilter
    EXPECT_TRUE(bloomFilter.contain(hash.getResults(url)));
}

TEST(AddCommandTest, BloomFilterSetsOnlyHashResultBits) {
    remove("bloom.txt"); // Start with clean BloomFilter file

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8); // Assume 8-bit filter
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);
    AddCommand addCommand(bloomFilter, blackList);

    Url url("www.precise.com");
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

    // Get the actual indices that should be set
    vector<int> expectedIndices = hash.execute(url);

    addCommand.execute(url, hash);

    // Load the BloomFilter state from file
    vector<int> bits = bloomStorage.load();

    // Check that only the expected indices are set to 1
    for (int i = 0; i < bits.size(); ++i) {
        bool shouldBeSet = std::find(expectedIndices.begin(), expectedIndices.end(), i) != expectedIndices.end();
        EXPECT_EQ(bits[i], shouldBeSet ? 1 : 0) << "Bit mismatch at index " << i;
    }
}
