#include <gtest/gtest.h>
#include <memory>
#include <vector>
#include "../src/logic/ContainCommand.h"
#include "../src/storage/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/storage/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(ContainCommandTest, NotInBloomReturnsFalseFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    ContainCommand contain(bloomFilter, blackList);

    Url url("www.unknown.com");
    std::vector<int> config = {1, 2};
    Hash hash(config, 8);
    contain.execute(url, hash);

    const bool* result = contain.getLastResult();
    EXPECT_FALSE(result[0]); // not in Bloom
    EXPECT_FALSE(result[1]); // not in BlackList
}


TEST(ContainCommandTest, InBloomButNotInBlackListReturnsTrueFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.false-positive.com");
    std::vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));

    ContainCommand contain(bloomFilter, blackList);
    contain.execute(url, hash);

    const bool* result = contain.getLastResult();
    EXPECT_TRUE(result[0]);
    EXPECT_FALSE(result[1]);
}


TEST(ContainCommandTest, InBothReturnsTrueTrue) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.true-true.com");
    std::vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));
    blackList.add(url);

    ContainCommand contain(bloomFilter, blackList);
    contain.execute(url, hash);

    const bool* result = contain.getLastResult();
    EXPECT_TRUE(result[0]);
    EXPECT_TRUE(result[1]);
}

