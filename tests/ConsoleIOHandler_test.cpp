#include <gtest/gtest.h>
#include "../src/io/ConsoleIOHandler.h"


// ===================== READLINE TESTS =====================

TEST(ConsoleIOHandlerTest, ReadsLineCorrectly) {
    // Simulate input with two lines
    std::istringstream input("hello line\nsecond line\n");
    std::ostringstream output;
    ConsoleIOHandler handler(input, output);

    // Read lines from the input
    std::string first = handler.readLine();
    std::string second = handler.readLine();

    // Assert correct content
    EXPECT_EQ(first, "hello line");
    EXPECT_EQ(second, "second line");
}

// ===================== WRITELINE TESTS =====================

TEST(ConsoleIOHandlerTest, WritesLineCorrectly) {
    // No input needed for writing test
    std::istringstream input("");
    std::ostringstream output;
    ConsoleIOHandler handler(input, output);

    // Write two lines
    handler.writeLine("output line one");
    handler.writeLine("output line two");

    // Assert that the output contains the expected lines with newlines
    EXPECT_EQ(output.str(), "output line one\noutput line two\n");
}

// ===================== COMBINED READ/WRITE TESTS =====================

TEST(ConsoleIOHandlerTest, ReadThenWriteEchoesInput) {
    // Simulate input stream with one line
    std::istringstream input("echo me\n");
    std::ostringstream output;
    ConsoleIOHandler handler(input, output);

    // Read from input, then write modified output
    std::string line = handler.readLine();
    handler.writeLine("echo: " + line);

    // Assert that the output reflects the echoed input
    EXPECT_EQ(output.str(), "echo: echo me\n");
}
