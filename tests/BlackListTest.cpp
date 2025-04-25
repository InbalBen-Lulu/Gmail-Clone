#include <gtest/gtest.h>
#include <fstream>
#include "../src/data/BlackList.h"
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(BlackListTest, UrlAddedAppearsInList) {
    BlackListStorage storage(true);
    BlackList bl(storage);
    Url url("www.test.com");
    bl.add(url);

    EXPECT_TRUE(bl.contains(url));
}

TEST(BlackListTest, UrlNotAddedReturnsFalse) {
    BlackListStorage storage(true);
    BlackList bl(storage);
    Url url("www.unknown.com");

    EXPECT_FALSE(bl.contains(url));
}


TEST(BlackListTest, AddPersistsToStorageFile) {
    // Clear the storage file before running the test
    ofstream("../data/blacklist.txt", ios::trunc).close();

    BlackListStorage storage(true);
    BlackList blackList(storage);

    Url url("www.test.com");
    blackList.add(url);

    // Use load() from BlackListStorage to verify persistence
    set<Url> loadedUrls = storage.load();
    EXPECT_TRUE(loadedUrls.find(url) != loadedUrls.end());
}
