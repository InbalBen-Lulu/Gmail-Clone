#include <gtest/gtest.h>
#include "Url.h"

TEST(UrlTest, ValidUrlReturnsTrue) {
    Url url("www.example.com");
    EXPECT_TRUE(url.isValid());
}

TEST(UrlTest, EmptyUrlReturnsFalse) {
    Url url("");
    EXPECT_FALSE(url.isValid());
}

TEST(UrlTest, InvalidCharactersReturnFalse) {
    Url url("ht@tp://inv@lid.url");
    EXPECT_FALSE(url.isValid());
}

TEST(UrlTest, UrlComparisonWorks) {
    Url url1("a.com");
    Url url2("b.com");
    EXPECT_TRUE(url1 < url2);
}
