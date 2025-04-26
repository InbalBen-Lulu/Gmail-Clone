#include <gtest/gtest.h>
#include <memory>
#include <cstdio>
#include <vector>
#include <algorithm>
#include "../src/commands/AddCommand.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"
#include "../src/io/IIOHandler.h"

using namespace std;

// Mock IO handler (minimal stub)
class DummyIOHandler : public IIOHandler {
    public:
        string readLine() override { return ""; }
        void writeLine(const string& line) override {}
};

TEST(AddCommandTest, ExecuteAddsToBlackListAndBloomFilter) {
    remove("bloom.txt"); // start with clean file

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);
    DummyIOHandler io;

    AddCommand addCommand(bloomFilter, blackList);

    Url url("www.check.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    addCommand.execute(url, hash, io);

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
    DummyIOHandler io;

    Url url("www.check.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    addCommand.execute(url, hash, io);

    // Check if corresponding bits are set in BloomFilter
    EXPECT_TRUE(bloomFilter.contain(hash.execute(url)));
}

TEST(AddCommandTest, BloomFilterSetsCorrectBits) {
    remove("bloom.txt"); // Start with clean BloomFilter file

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8); // 8-bit Bloom filter
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);
    AddCommand addCommand(bloomFilter, blackList);
    DummyIOHandler io;

    Url url("www.precise.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    // Get the expected bit vector from the hash function
    vector<int> expectedBits = hash.execute(url);

    // Execute the add command
    addCommand.execute(url, hash, io);

    // Load the BloomFilter state after insertion
    vector<int> actualBits = bloomStorage.load();

    // Compare expected and actual bit vectors
    EXPECT_EQ(actualBits.size(), expectedBits.size()) << "BloomFilter size mismatch";

    for (int i = 0; i < actualBits.size(); ++i) {
        EXPECT_EQ(actualBits[i], expectedBits[i]) << "Mismatch at bit " << i;
    }
}
