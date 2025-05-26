#include <gtest/gtest.h>
#include <memory>
#include <cstdio>
#include <vector>
#include <algorithm>
#include "../src/commands/PostCommand.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(PostCommandTest, ExecuteAddsToBlackListAndReturns201) {
    remove("bloom.txt"); // Start with clean state

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);

    std::mutex mutex;
    PostCommand postCommand(bloomFilter, blackList, mutex);

    Url url("www.check.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    string result = postCommand.execute(url, hash);

    EXPECT_TRUE(blackList.contains(url));
    EXPECT_EQ(result, "201 Created");
}

TEST(PostCommandTest, ExecuteSetsBitsInBloomFilter) {
    remove("bloom.txt");

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);

    std::mutex mutex;
    PostCommand postCommand(bloomFilter, blackList, mutex);

    Url url("www.check.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    string result = postCommand.execute(url, hash);

    EXPECT_TRUE(bloomFilter.contain(hash.execute(url)));
    EXPECT_EQ(result, "201 Created");
}

TEST(PostCommandTest, ExecuteAffectsBothStructures) {
    remove("bloom.txt");

    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blackListStorage(true);
    BlackList blackList(blackListStorage);

    std::mutex mutex;
    PostCommand postCommand(bloomFilter, blackList, mutex);

    Url url("www.fullcheck.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    postCommand.execute(url, hash);

    EXPECT_TRUE(blackList.contains(url));
    EXPECT_TRUE(bloomFilter.contain(hash.execute(url)));
}

