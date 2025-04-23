#include <gtest/gtest.h>
#include <memory>
#include "ContainCommand.h"
#include "BloomFilter.h"
#include "BlackList.h"
#include "BloomStorage.h"
#include "BlackListStorage.h"
#include "Hash.h"
#include "Url.h"

using namespace std;

TEST(ContainCommandTest, NotInBloomReturnsFalseFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    ContainCommand contain(bloomFilter, blackList);

    Url url("www.unknown.com");
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

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
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

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
    unique_ptr<int[]> hashArray(new int[2]{1, 2});
    Hash hash(move(hashArray));

    bloomFilter.add(hash.execute(url));
    blackList.add(url);

    ContainCommand contain(bloomFilter, blackList);
    contain.execute(url, hash);

    const bool* result = contain.getLastResult();
    EXPECT_TRUE(result[0]);
    EXPECT_TRUE(result[1]);
}

