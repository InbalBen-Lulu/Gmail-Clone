#include <gtest/gtest.h>
#include "../src/commands/DeleteCommand.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(DeleteCommandTest, DeletesExistingUrlAndReturns204) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.to-delete.com");
    blackList.add(url);
    
    std::mutex mutex;
    DeleteCommand delCommand(bloomFilter, blackList, mutex);
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    string result = delCommand.execute(url, hash);

    EXPECT_EQ(result, "204 No Content");
    EXPECT_FALSE(blackList.contains(url));
}

TEST(DeleteCommandTest, DeletingNonExistingUrlReturns404) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.not-there.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    std::mutex mutex;
    DeleteCommand delCommand(bloomFilter, blackList, mutex);
    string result = delCommand.execute(url, hash);

    EXPECT_EQ(result, "404 Not Found");
}

TEST(DeleteCommandTest, DeletingTwiceReturns404SecondTime) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.once-only.com");
    blackList.add(url);

    std::mutex mutex;
    DeleteCommand delCommand(bloomFilter, blackList, mutex);
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    string first = delCommand.execute(url, hash);
    string second = delCommand.execute(url, hash);

    EXPECT_EQ(first, "204 No Content");
    EXPECT_EQ(second, "404 Not Found");
}

TEST(DeleteCommandTest, DoesNotAffectBloomFilter) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.safe-bloom.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));
    blackList.add(url);

    DeleteCommand delCommand(bloomFilter, blackList);
    delCommand.execute(url, hash);

    // still should be in bloom filter
    EXPECT_TRUE(bloomFilter.contain(hash.execute(url)));
}
