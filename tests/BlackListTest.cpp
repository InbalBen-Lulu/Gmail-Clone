#include <gtest/gtest.h>
#include <fstream>
#include "BlackList.h"
#include "BlackListStorage.h"
#include "Url.h"

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
    ofstream("blacklist.txt", ios::trunc).close();

    BlackListStorage storage(true);
    BlackList blackList(storage);

    Url url("www.test.com");
    blackList.add(url);

    // Check if the URL was written to the file
    ifstream inFile("blacklist.txt");
    ASSERT_TRUE(inFile.is_open());

    string line;
    bool found = false;
    while (getline(inFile, line)) {
        if (line == url.getUrlPath()) {
            found = true;
            break;
        }
    }

    inFile.close();
    EXPECT_TRUE(found);
}
