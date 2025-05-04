#include <gtest/gtest.h>
#include <memory>
#include <cstdio>
#include <vector>
#include <algorithm>
#include "../src/commands/GetCommand.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(GetCommandTest, NotInBloomReturnsFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    GetCommand get(bloomFilter, blackList);

    Url url("www.unknown.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    string result = get.execute(url, hash);

    EXPECT_EQ(result, "200 Ok\n\nfalse");
}

TEST(GetCommandTest, InBloomButNotInBlackListReturnsTrueFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.false-positive.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));

    GetCommand get(bloomFilter, blackList);
    string result = get.execute(url, hash);

    EXPECT_EQ(result, "200 Ok\n\ntrue false");
}

TEST(GetCommandTest, InBothReturnsTrueTrue) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.true-true.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));
    blackList.add(url);

    GetCommand get(bloomFilter, blackList);
    string result = get.execute(url, hash);

    EXPECT_EQ(result, "200 Ok\n\ntrue true");
}
