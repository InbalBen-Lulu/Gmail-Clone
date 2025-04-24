#include <gtest/gtest.h>
#include "../src/utils/Url.h"

TEST(UrlTest, UrlComparisonWorks) {
    Url url1("a.com");
    Url url2("b.com");
    EXPECT_TRUE(url1 < url2);
}
