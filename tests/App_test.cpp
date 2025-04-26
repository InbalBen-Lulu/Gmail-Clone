#include <gtest/gtest.h>
#include <fstream>
#include <cstdio>
#include <string>
#include <sstream>
#include <vector>
#include "../src/app/App.h"


// ===================== APP::CONSTRUCTION TEST =====================

TEST(AppTest, ConstructorInitializesWithoutException) {
    EXPECT_NO_THROW({
        App app;
    });
}

// ===================== APP::RUN TESTS =====================

TEST(AppTest, ValidInitAndSimpleCommands) {
    // Prepare input file with 4 lines
    std::ofstream in("test_input.txt");
    in << "128 1 2\n";             // Initialization line
    in << "1 www.example.com0\n";   // Add command
    in << "2 www.example.com0\n";   // Contain command (should find the added URL)
    in << "2 www.example.com123\n"; // Contain command (should not find the URL)
    in.close();

    // Redirect input and output to files
    freopen("test_input.txt", "r", stdin);
    freopen("test_output.txt", "w", stdout);

    // Run the application
    App app;
    app.run();

    // Restore standard input and output
#ifdef _WIN32
    freopen("CON", "r", stdin);
    freopen("CON", "w", stdout);
#else
    freopen("/dev/tty", "r", stdin);
    freopen("/dev/tty", "w", stdout);
#endif

    // Read output lines
    std::ifstream out("test_output.txt");
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(out, line)) {
        lines.push_back(line);
    }
    out.close();

    // Clean up temporary files
    std::remove("test_input.txt");
    std::remove("test_output.txt");

    // Verify the output
    ASSERT_EQ(lines.size(), 2); // Should produce exactly 2 output lines

    EXPECT_EQ(lines[0], "true true"); // First check should succeed (URL was added)
    // The second URL was not added; it could be a false positive on BloomFilter
    EXPECT_TRUE(lines[1] == "false" || lines[1] == "true false");
}

TEST(AppTest, InitAndInvalidCommand) {
    // Prepare input file with 4 lines
    std::ofstream in("test_input.txt");
    in << "a\n";                   // Invalid line (should be ignored)
    in << "8 1 2\n";                // Valid initialization line
    in << "2 www.example.com0\n";   // Contain command (should fail, URL not added)
    in << "x\n";                    // Invalid command (should be ignored)
    in.close();

    // Redirect input and output to files
    freopen("test_input.txt", "r", stdin);
    freopen("test_output.txt", "w", stdout);

    // Run the application
    App app;
    app.run();

    // Restore standard input and output
#ifdef _WIN32
    freopen("CON", "r", stdin);
    freopen("CON", "w", stdout);
#else
    freopen("/dev/tty", "r", stdin);
    freopen("/dev/tty", "w", stdout);
#endif

    // Read output lines
    std::ifstream out("test_output.txt");
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(out, line)) {
        lines.push_back(line);
    }
    out.close();

    // Clean up temporary files
    std::remove("test_input.txt");
    std::remove("test_output.txt");

    // Verify the output
    ASSERT_EQ(lines.size(), 1); // Should produce exactly 1 output line
    EXPECT_EQ(lines[0], "false"); // Only a single 'false' should be printed
}


TEST(AppTest, CommandsOnExampleComVariants) {
    std::ofstream in("test_input.txt");
    in << "1 www.example.com0\n";
    in << "2 www.example.com0\n";
    in << "2 www.example.com1\n";
    in << "2 www.example.com11\n";
    in.close();

    freopen("test_input.txt", "r", stdin);
    freopen("test_output.txt", "w", stdout);

    App app;
    app.run();

#ifdef _WIN32
    freopen("CON", "r", stdin);
    freopen("CON", "w", stdout);
#else
    freopen("/dev/tty", "r", stdin);
    freopen("/dev/tty", "w", stdout);
#endif

    std::ifstream out("test_output.txt");
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(out, line)) {
        lines.push_back(line);
    }
    out.close();
    std::remove("test_input.txt");
    std::remove("test_output.txt");

    EXPECT_EQ(lines.size(), 3);
    EXPECT_EQ(lines[0], "true true"); // The added URL should be found

    // The following URLs were not added; expect "false" or "true false" due to Bloom filter behavior
    for (size_t i = 1; i < lines.size(); ++i) {
        EXPECT_TRUE(lines[i] == "false" || lines[i] == "true false");
    }
}


TEST(AppTest, RepeatsUntilValidInitLine) {
    std::ofstream in("test_input.txt");
    in << "invalid\n";
    in << "still wrong\n";
    in << "256 2\n";
    in << "2 www.example.com\n";
    in.close();

    freopen("test_input.txt", "r", stdin);
    freopen("test_output.txt", "w", stdout);

    App app;
    app.run();

#ifdef _WIN32
    freopen("CON", "r", stdin);
    freopen("CON", "w", stdout);
#else
    freopen("/dev/tty", "r", stdin);
    freopen("/dev/tty", "w", stdout);
#endif

    std::ifstream out("test_output.txt");
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(out, line)) {
        lines.push_back(line);
    }
    out.close();
    std::remove("test_input.txt");
    std::remove("test_output.txt");

    // Should have exactly 1 valid response (everything before the 3rd line was ignored)
    EXPECT_EQ(lines.size(), 1);
    EXPECT_TRUE(lines[0] == "false");
}

TEST(AppTest, RejectsNegativeOrZeroInInitLine) {
    std::ofstream in("test_input.txt");
    in << "0 -1 -2\n";  // invalid: 0 and negative numbers
    in << "3 10 -5 20\n";  // invalid: negative hash count
    in << "4 1 2 3\n";  // valid fallback
    in << "2 www.example.com\n";
    in.close();

    freopen("test_input.txt", "r", stdin);
    freopen("test_output.txt", "w", stdout);

    App app;
    app.run();

#ifdef _WIN32
    freopen("CON", "r", stdin);
    freopen("CON", "w", stdout);
#else
    freopen("/dev/tty", "r", stdin);
    freopen("/dev/tty", "w", stdout);
#endif

    std::ifstream out("test_output.txt");
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(out, line)) {
        lines.push_back(line);
    }
    out.close();
    std::remove("test_input.txt");
    std::remove("test_output.txt");

    // Only the last init line should be accepted, so only 1 response expected
    EXPECT_EQ(lines.size(), 1);
    EXPECT_TRUE(lines[0] == "false");
}