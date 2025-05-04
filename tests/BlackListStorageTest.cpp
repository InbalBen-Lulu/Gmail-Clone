#include <gtest/gtest.h>
#include <fstream>
#include <cstdio>
#include "../src/storage/BlackListStorage.h"
#include "../src/utils/Url.h"

using namespace std;

TEST(BlackListStorageTest, AddUrlAppendsToFile) {
    remove("../data/blacklist.txt"); // start clean

    BlackListStorage storage(true);
    Url url("www.blacklisted.com");
    storage.add(url);

    ifstream file("../data/blacklist.txt");
    ASSERT_TRUE(file.is_open());

    string line;
    bool found = false;
    while (getline(file, line)) {
        if (line == url.getUrlPath()) {
            found = true;
            break;
        }
    }
    file.close();
    EXPECT_TRUE(found);
}

TEST(BlackListStorageTest, LoadReturnsAllUrls) {
    remove("../data/blacklist.txt");

    BlackListStorage storage(true);
    Url url1("www.a.com"), url2("www.b.com");
    storage.add(url1);
    storage.add(url2);

    set<Url> urls = storage.load();
    EXPECT_TRUE(urls.find(url1) != urls.end());
    EXPECT_TRUE(urls.find(url2) != urls.end());
}


TEST(BlackListStorageTest, DataIsPersistentAcrossInstances) {
    remove("../data/blacklist.txt");

    Url url1("www.persist-a.com");
    Url url2("www.persist-b.com");

    // Create first instance and add URLs
    {
        BlackListStorage storage(true);
        storage.add(url1);
        storage.add(url2);
    }

    // Create new instance that loads from file
    BlackListStorage storage(false);
    set<Url> loaded = storage.load();

    // Check that both URLs are found
    EXPECT_TRUE(loaded.find(url1) != loaded.end());
    EXPECT_TRUE(loaded.find(url2) != loaded.end());
    EXPECT_EQ(loaded.size(), 2);
}

TEST(BlackListStorageTest, LoadEmptyFileReturnsEmptySet) {
    ofstream("../data/blacklist.txt", ios::trunc).close(); // create empty file

    BlackListStorage storage(false);
    set<Url> urls = storage.load();

    EXPECT_TRUE(urls.empty());
}

TEST(BlackListStorageTest, DeleteUrlRemovesLineFromFile) {
    remove("../data/blacklist.txt");

    Url url1("www.to-keep.com");
    Url url2("www.to-delete.com");

    // Add two URLs
    {
        BlackListStorage storage(true);
        storage.add(url1);
        storage.add(url2);
    }

    // Delete url2
    {
        BlackListStorage storage(false);
        storage.deleteUrl(url2);
    }

    // Check file contents
    ifstream file("../data/blacklist.txt");
    ASSERT_TRUE(file.is_open());

    string line;
    bool foundDeleted = false;
    bool foundKept = false;

    while (getline(file, line)) {
        if (line == url2.getUrlPath()) foundDeleted = true;
        if (line == url1.getUrlPath()) foundKept = true;
    }

    file.close();
    EXPECT_FALSE(foundDeleted);
    EXPECT_TRUE(foundKept);
}

TEST(BlackListStorageTest, DeleteNonExistingUrlDoesNothing) {
    remove("../data/blacklist.txt");

    Url url("www.not-there.com");

    BlackListStorage storage(true);
    EXPECT_NO_THROW(storage.deleteUrl(url));

    ifstream file("../data/blacklist.txt");
    ASSERT_TRUE(file.is_open());
    EXPECT_TRUE(file.peek() == ifstream::traits_type::eof()); // file is empty
}
