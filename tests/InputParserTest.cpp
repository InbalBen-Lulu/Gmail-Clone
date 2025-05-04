#include <gtest/gtest.h>
#include "../src/parser/InputParser.h"

// ===================== VALID INPUT TESTS =====================
TEST(InputParserTest, ValidInputWithGET) {
    std::string input = "GET http://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "GET");
    EXPECT_EQ(result->url.getUrlPath(), "http://example.com");
}

TEST(InputParserTest, ValidInputWithPOST) {
    std::string input = "POST https://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "POST");
    EXPECT_EQ(result->url.getUrlPath(), "https://example.com");
}

TEST(InputParserTest, ValidUrlWithSpecialCharacters) {
    std::string input = "DELETE https://example.com/path?query=123&other=456";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "DELETE");
    EXPECT_EQ(result->url.getUrlPath(), "https://example.com/path?query=123&other=456");
}

TEST(InputParserTest, ValidUrlWithPort) {
    std::string input = "POST http://example.com:8080";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "POST");
    EXPECT_EQ(result->url.getUrlPath(), "http://example.com:8080");
}

TEST(InputParserTest, UrlMissingProtocol_StillValid) {
    std::string input = "GET example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
}

// ===================== INVALID INPUT TESTS =====================
TEST(InputParserTest, InvalidCommand) {
    std::string input = "PING http://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

TEST(InputParserTest, InvalidUrl) {
    std::string input = "POST invalid-url";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

TEST(InputParserTest, InvalidUrlStructure) {
    std::string input = "GET http:// example";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

// ===================== EDGE CASES =====================
TEST(InputParserTest, ExtraSpacesBeforeCommand) {
    std::string input = "   DELETE http://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "DELETE");
    EXPECT_EQ(result->url.getUrlPath(), "http://example.com");
}

TEST(InputParserTest, ExtraSpacesAfterUrl) {
    std::string input = "POST http://example.com   ";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->url.getUrlPath(), "http://example.com");
}

TEST(InputParserTest, MultipleSpacesBetweenCommandAndUrl) {
    std::string input = "  GET     https://multiple-spaces.com  ";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
    EXPECT_EQ(result->command, "GET");
    EXPECT_EQ(result->url.getUrlPath(), "https://multiple-spaces.com");
}

// ===================== MISSING OR INVALID URL TESTS =====================
TEST(InputParserTest, MissingUrl) {
    std::string input = "POST";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

TEST(InputParserTest, MissingCommand) {
    std::string input = "   http://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

TEST(InputParserTest, MultipleUrls) {
    std::string input = "GET http://example.com http://another.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());
}

// ===================== TESTS FOR parseInitLine =====================
TEST(InputParserTest, ValidParseInitLine) {
    const char* argv[] = {"program", "1234", "5", "10", "15", "20"};
    int argc = 6;
    int port;
    size_t size;
    std::vector<int> hashes;

    bool result = InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes);

    ASSERT_TRUE(result);
    EXPECT_EQ(port, 1234);
    EXPECT_EQ(size, 5);
    EXPECT_EQ(hashes, std::vector<int>({10, 15, 20}));
}

TEST(InputParserTest, ValidSingleNumber) {
    const char* argv[] = {"program", "1234", "1", "10"};
    int argc = 4;
    int port;
    size_t size;
    std::vector<int> hashes;

    bool result = InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes);

    ASSERT_TRUE(result);
    EXPECT_EQ(hashes.size(), 1);
    EXPECT_EQ(hashes[0], 10);
}

// ===================== INVALID parseInitLine TESTS =====================
TEST(InputParserTest, ZeroSizeInvalid) {
    const char* argv[] = {"program", "2345", "0", "1", "2"};
    int argc = 5;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

TEST(InputParserTest, NegativeHashCount) {
    const char* argv[] = {"program", "1234", "3", "1", "-2", "3"};
    int argc = 6;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

TEST(InputParserTest, InvalidSizeNotNumber) {
    const char* argv[] = {"program", "1234", "abc", "10", "20"};
    int argc = 5;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

TEST(InputParserTest, PortOutOfRange_high) {
    const char* argv[] = {"program", "999999", "3", "1", "2", "3"};
    int argc = 6;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

TEST(InputParserTest, PortOutOfRange_low) {
    const char* argv[] = {"program", "-1", "3", "1", "2", "3"};
    int argc = 6;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

TEST(InputParserTest, MissingArguments) {
    const char* argv[] = {"program", "1234", "5"};
    int argc = 3;
    int port;
    size_t size;
    std::vector<int> hashes;

    ASSERT_FALSE(InputParser::parseInitLine(argc, const_cast<char**>(argv), port, size, hashes));
}

// ===================== CLEAN FUNCTION TESTS =====================
TEST(InputParserTest, Clean_ValidInputWithSingleSpaces) {
    std::string input = "  8  1  ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "8 1");
}

TEST(InputParserTest, Clean_LeadingTrailingSpaces) {
    std::string input = "    5 7  ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "5 7");
}

TEST(InputParserTest, Clean_SingleNumber) {
    std::string input = " 8 ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "8");
}

TEST(InputParserTest, Clean_MultipleSpacesBetweenNumbers) {
    std::string input = "8      1  ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "8 1");
}

TEST(InputParserTest, Clean_NoExtraSpaces) {
    std::string input = "10 20";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "10 20");
}

TEST(InputParserTest, Clean_OnlySpaces) {
    std::string input = "       ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "");
}

TEST(InputParserTest, Clean_WithLettersAndSpaces) {
    std::string input = "8 a 1";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "8 a 1");
}

TEST(InputParserTest, Clean_InitLineWithExtraSpaces) {
    std::string input = "   1234     5     10   20 30    ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "1234 5 10 20 30");
}

TEST(InputParserTest, Clean_CommandLineWithQueryParams) {
    std::string input = "  DELETE   https://example.com/path?x=1&y=2   ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "DELETE https://example.com/path?x=1&y=2");
}

TEST(InputParserTest, Clean_CommandLineWithSpaces) {
    std::string input = "   GET      http://example.com  ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "GET http://example.com");
}

TEST(InputParserTest, Clean_InitLineWithExtraSpaces) {
    std::string input = "   1234     5     10   20 30    ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "1234 5 10 20 30");
}

TEST(InputParserTest, Clean_CommandWithBrokenUrl) {
    std::string input = "GET http://exam ple.com";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "GET http://exam ple.com"); 
}

TEST(InputParserTest, Clean_InputWithSymbolsOnly) {
    std::string input = "  *** ###  ";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "*** ###"); 
}

TEST(InputParserTest, Clean_InitLineWithLettersInsteadOfNumbers) {
    std::string input = "port size ten twenty";
    std::string cleaned = InputParser::clean(input);
    EXPECT_EQ(cleaned, "port size ten twenty");
}