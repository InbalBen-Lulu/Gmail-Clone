#include <gtest/gtest.h>
#include "../src/parser/InputParser.h"

// ===================== VALID INPUT TESTS ===================== 
TEST(InputParserTest, ValidInputWithCommand1) {
    std::string input = "1 http://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Check if the result is valid
    EXPECT_EQ(result.value().commandId, 1);  // Validate the command ID
    EXPECT_EQ(result.value().url, "http://example.com");  // Validate the URL
}

TEST(InputParserTest, ValidInputWithCommand2) {
    std::string input = "2 https://example.com";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Check if the result is valid
    EXPECT_EQ(result.value().commandId, 2);  // Validate the command ID
    EXPECT_EQ(result.value().url, "https://example.com");  // Validate the URL
}

TEST(InputParserTest, ValidUrlWithSpecialCharacters) {
    std::string input = "1 https://example.com/path?query=123&other=456";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Ensure the result is valid
    EXPECT_EQ(result.value().commandId, 1);  // Command ID should be 1
    EXPECT_EQ(result.value().url, "https://example.com/path?query=123&other=456");  // Ensure URL is as expected
}

TEST(InputParserTest, ValidUrlWithPort) {
    std::string input = "2 http://example.com:8080";
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Ensure the result is valid
    EXPECT_EQ(result.value().commandId, 2);  // Command ID should be 2
    EXPECT_EQ(result.value().url, "http://example.com:8080");  // URL should be http://example.com:8080
}

TEST(InputParserTest, InvalidUrlMissingProtocol) {
    std::string input = "1 example.com"; 
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());
}

// ===================== INVALID INPUT TESTS ===================== 
TEST(InputParserTest, InvalidCommand) {
    std::string input = "3 http://example.com";  // Invalid command ID
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // The result should be invalid
}

TEST(InputParserTest, InvalidUrl) {
    std::string input = "1 invalid-url";  // Invalid URL
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // The result should be invalid
}

TEST(InputParserTest, InvalidUrlInvalidStructure) {
    std::string input = "1 http:// example";  // Invalid URL structure (space in between)
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // The result should be invalid due to improper URL structure
}

// ===================== EDGE CASES ===================== 
TEST(InputParserTest, ExtraSpacesBeforeCommand) {
    std::string input = "  1 http://example.com";  // Extra spaces before the command
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Should pass since spaces before the command are allowed
    EXPECT_EQ(result.value().commandId, 1);  
}

TEST(InputParserTest, ExtraSpacesAfterUrl) {
    std::string input = "1 http://example.com   ";  // Extra spaces after the URL
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // Should pass since spaces after the URL are allowed
    EXPECT_EQ(result.value().url, "http://example.com"); 
}

TEST(InputParserTest, CommandWithMultipleSpaces) {
    std::string input = "  2     https://multiple-spaces.com  ";  // Input with multiple spaces
    auto result = InputParser::parseCommandLine(input);

    ASSERT_TRUE(result.has_value());  // The result should be valid after cleaning spaces
    EXPECT_EQ(result.value().commandId, 2);  // Command ID should be 2
    EXPECT_EQ(result.value().url, "https://multiple-spaces.com");  // URL should be cleaned
}

// ===================== MISSING OR INVALID URL TESTS ===================== 
TEST(InputParserTest, MissingUrl) {
    std::string input = "1 ";  // Missing URL after the command
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // The result should be invalid due to missing URL
}

TEST(InputParserTest, MissingCommand) {
    std::string input = "  http://missing-command.com";  // Missing command before the URL
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // Should be invalid due to missing command
}

TEST(InputParserTest, MultipleUrls) {
    std::string input = "1 http://example.com http://another-url.com";  // Two URLs but only one expected
    auto result = InputParser::parseCommandLine(input);

    ASSERT_FALSE(result.has_value());  // Should be invalid since there's more than one URL
}

// ===================== TEST FOR parseInitLine ===================== 
TEST(InputParserTest, ValidParseInitLine) {
    std::string input = "5 10 15 20";
    size_t expectedSize = 5;
    std::vector<int> expectedVector = {10, 15, 20};
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_TRUE(result);  // The result should be true
    EXPECT_EQ(expectedSize, 5);  // Ensure the size is correct
    EXPECT_EQ(expectedVector.size(), 3);  // Ensure the vector contains 3 elements
    EXPECT_EQ(expectedVector[0], 10);  // Ensure the first value is correct
    EXPECT_EQ(expectedVector[1], 15);  // Ensure the second value is correct
    EXPECT_EQ(expectedVector[2], 20);  // Ensure the third value is correct
}

TEST(InputParserTest, ValidSingleNumber) {
    std::string input = "1 10";  // Only one number
    size_t expectedSize = 1;
    std::vector<int> expectedVector = {10};
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_TRUE(result);  // Should return true
    EXPECT_EQ(expectedVector.size(), 1);  // The vector should contain one element
    EXPECT_EQ(expectedVector[0], 10);  // The element should be 10
}

// ===================== INVALID parseInitLine TESTS ===================== 
TEST(InputParserTest, InitLineWithZeroSize) {
    std::string input = "0 1 2 3";
    size_t expectedSize = 0;
    std::vector<int> expectedVector;

    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);

    ASSERT_FALSE(result);  // Invalid because size must be positive
}

TEST(InputParserTest, InitLineWithNegativeHashCount) {
    std::string input = "4 1 -2 3";
    size_t expectedSize = 4;
    std::vector<int> expectedVector;

    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);

    ASSERT_FALSE(result);  // Invalid because of negative number
}

TEST(InputParserTest, InitLineWithMixedValidAndNegativeValues) {
    std::string input = "3 -1 2 -3";
    size_t expectedSize = 3;
    std::vector<int> expectedVector;

    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);

    ASSERT_FALSE(result);  // Still invalid if any number is negative
}

TEST(InputParserTest, InvalidParseInitLine) {
    std::string input = "five 10 15 20";  // "five" is not a valid number
    size_t expectedSize = 0;
    std::vector<int> expectedVector;
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_FALSE(result);  // The result should be false due to invalid input
}

TEST(InputParserTest, EmptyInput) {
    std::string input = "";  // Empty input
    size_t expectedSize = 0;
    std::vector<int> expectedVector;
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_FALSE(result);  // The result should be false for empty input
}

TEST(InputParserTest, InputWithExtraSpaces) {
    std::string input = "  5   10   15   20  ";  // Input with extra spaces
    size_t expectedSize = 5;
    std::vector<int> expectedVector;
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_TRUE(result);  // The result should be true after trimming spaces
    EXPECT_EQ(expectedSize, 5);
    EXPECT_EQ(expectedVector.size(), 3);
}

TEST(InputParserTest, SingleNumberInput) {
    std::string input = "1";
    size_t expectedSize = 1;
    std::vector<int> expectedVector;
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_FALSE(result);  // The result should be false since at least one more number is needed
}

TEST(InputParserTest, InvalidSizeNonNumeric) {
    std::string input = "abc 10 15 20";  // "abc" is not a valid size
    size_t expectedSize = 0;
    std::vector<int> expectedVector;
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_FALSE(result);  // The result should be false due to invalid size
}

TEST(InputParserTest, VeryLargeNumber) {
    std::string input = "5 1000000000 1000000000 1000000000 1000000000 1000000000";
    size_t expectedSize = 5;
    std::vector<int> expectedVector = {1000000000, 1000000000, 1000000000, 1000000000, 1000000000};
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_TRUE(result);  // Should return true for large numbers
    EXPECT_EQ(expectedVector[0], 1000000000);  // Validate large number
}

TEST(InputParserTest, LeadingZeroInNumbers) {
    std::string input = "3 01 02 03";  // Numbers with leading zeros
    size_t expectedSize = 3;
    std::vector<int> expectedVector = {1, 2, 3};  // Leading zeros should not affect the value
    
    bool result = InputParser::parseInitLine(input, expectedSize, expectedVector);
    
    ASSERT_TRUE(result);  // The result should be valid
    EXPECT_EQ(expectedVector[0], 1);  // Should ignore the leading zero
    EXPECT_EQ(expectedVector[1], 2);  // Should ignore the leading zero
    EXPECT_EQ(expectedVector[2], 3);  // Should ignore the leading zero
}

// ===================== CLEAN FUNCTION TESTS =====================

TEST(InputParserTest, Clean_ValidInputWithSingleSpaces) {
    std::string input = "  8  1  ";  // Input with multiple spaces between numbers
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "8 1");  // Should remove extra spaces between numbers, leaving only one space between them
}

TEST(InputParserTest, Clean_LeadingAndTrailingSpaces) {
    std::string input = "    5 7  ";  // Leading and trailing spaces
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "5 7");  // Should remove leading and trailing spaces, keeping one space between numbers
}

TEST(InputParserTest, Clean_SingleNumber) {
    std::string input = " 8 ";  // Single number with spaces around it
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "8");  // Should return the number without spaces
}

TEST(InputParserTest, Clean_MultipleSpacesBetweenNumbers) {
    std::string input = "8      1  ";  // Extra spaces between numbers
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "8 1");  // Should remove the extra spaces between numbers
}

TEST(InputParserTest, Clean_NoExtraSpaces) {
    std::string input = "10 20";  // No extra spaces
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "10 20");  // Should remain the same since no extra spaces exist
}

TEST(InputParserTest, Clean_OnlySpaces) {
    std::string input = "       ";  // Only spaces
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "");  // Should return an empty string if only spaces are provided
}

TEST(InputParserTest, Clean_InvalidInputWithLetters) {
    std::string input = "8 a 1";  // Invalid input containing letters
    std::string cleaned = InputParser::clean(input);

    ASSERT_EQ(cleaned, "8 a 1");  // Should return as is, because the clean function doesn't remove letters
}