#include <gtest/gtest.h>
#include <memory>
#include <cstdio>
#include <vector>
#include <algorithm>
#include "../src/commands/ContainCommand.h"
#include "../src/data/BloomFilter.h"
#include "../src/storage/BloomStorage.h"
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Hash.h"
#include "../src/utils/Url.h"
#include "../src/io/IIOHandler.h"

using namespace std;

class MockIOHandler : public IIOHandler {
public:
    std::stringstream output;
    string readLine() override { return ""; }
    void writeLine(const string& line) override { output << line << "\n"; }
};

TEST(ContainCommandTest, NotInBloomReturnsFalseFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    MockIOHandler io;
    ContainCommand contain(bloomFilter, blackList);

    Url url("www.unknown.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    contain.execute(url, hash, io);

    string result = io.output.str();
    EXPECT_NE(result.find("false"), string::npos);
}

TEST(ContainCommandTest, InBloomButNotInBlackListReturnsTrueFalse) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.false-positive.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));

    MockIOHandler io;
    ContainCommand contain(bloomFilter, blackList);
    contain.execute(url, hash, io);

    string result = io.output.str();
    EXPECT_NE(result.find("true"), string::npos);
    EXPECT_NE(result.find("false"), string::npos);
}

TEST(ContainCommandTest, InBothReturnsTrueTrue) {
    BloomStorage bloomStorage(true);
    BloomFilter bloomFilter(bloomStorage, 8);
    BlackListStorage blStorage(true);
    BlackList blackList(blStorage);

    Url url("www.true-true.com");
    vector<int> config = {1, 2};
    Hash hash(config, 8);

    bloomFilter.add(hash.execute(url));
    blackList.add(url);

    MockIOHandler io;
    ContainCommand contain(bloomFilter, blackList);
    contain.execute(url, hash, io);

    string result = io.output.str();
    EXPECT_NE(result.find("true"), string::npos);
}
