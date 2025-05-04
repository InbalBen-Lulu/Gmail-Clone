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

TEST(BlackListTest, DeleteUrlRemovesFromMemoryAndStorage) {
    // Check that deleteUrl removes a URL from internal set and file
    ofstream("../data/blacklist.txt", ios::trunc).close();

    BlackListStorage storage(true);
    BlackList blacklist(storage);

    Url url("www.to-delete.com");
    blacklist.add(url);
    EXPECT_TRUE(blacklist.contains(url));

    blacklist.deleteUrl(url);
    EXPECT_FALSE(blacklist.contains(url));

    set<Url> loaded = storage.load();
    EXPECT_TRUE(loaded.find(url) == loaded.end());
}

TEST(BlackListTest, AddDeletePersistsAcrossInstances) {
    // Add a URL, verify it persists, delete it, and verify removal persists too
    remove("../data/blacklist.txt");

    Url url("www.persistent-url.com");

    {
        BlackListStorage storage(true);
        BlackList blacklist(storage);
        blacklist.add(url);
        EXPECT_TRUE(blacklist.contains(url));
    }

    {
        BlackListStorage storage(false);
        BlackList blacklist(storage);
        EXPECT_TRUE(blacklist.contains(url));
        set<Url> loaded = storage.load();
        EXPECT_TRUE(loaded.find(url) != loaded.end());
        blacklist.deleteUrl(url);
    }

    {
        BlackListStorage storage(false);
        BlackList blacklist(storage);
        EXPECT_FALSE(blacklist.contains(url));
        set<Url> loaded = storage.load();
        EXPECT_TRUE(loaded.find(url) == loaded.end());
    }
}

TEST(BlackListTest, DeleteUrlRemovesOnlyTargetedUrl) {
    remove("../data/blacklist.txt");

    BlackListStorage storage(true);
    BlackList blackList(storage);

    Url url1("www.a.com");
    Url url2("www.b.com");
    Url url3("www.c.com");

    blackList.add(url1);
    blackList.add(url2);
    blackList.add(url3);

    // Delete url2
    blackList.deleteUrl(url2);

    // Ensure only url2 was removed
    EXPECT_TRUE(blackList.contains(url1));
    EXPECT_FALSE(blackList.contains(url2));
    EXPECT_TRUE(blackList.contains(url3));

    // Re-load using new instance to confirm persistence
    BlackListStorage newStorage(false);
    BlackList reloaded(newStorage);
    EXPECT_TRUE(reloaded.contains(url1));
    EXPECT_FALSE(reloaded.contains(url2));
    EXPECT_TRUE(reloaded.contains(url3));
}
