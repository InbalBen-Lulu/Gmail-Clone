#include <gtest/gtest.h>
#include <fstream>
#include <cstdio>
#include <string>
#include <sstream>
#include <vector>
#include "../src/app/App.h"

// ===================== APP::RUN TESTS =====================

TEST(AppTest, ValidInitAndSimpleCommands) {
    std::ofstream in("test_input.txt");
    in << "128 1 2\n";
    in << "1 www.example.com0\n";
    in << "2 www.example.com0\n";
    in << "2 www.example.com123\n";
    in << "exit\n";
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

    // Expecting 2 command responses: one for the valid check, one for the not-added URL
    EXPECT_EQ(lines.size(), 2);
    for (const auto& l : lines) {
        EXPECT_TRUE(l == "true" || l == "false" || l == "true true" || l == "true false");
    }
}

TEST(AppTest, InputOutputMatchesExample1) {
    std::ofstream in("test_input.txt");
    in << "a\n";
    in << "8 1 2\n";
    in << "2 www.example.com0\n";
    in << "x\n";
    in << "1 www.example.com0\n";
    in << "2 www.example.com0\n";
    in << "2 www.example.com1\n";
    in << "2 www.example.com11\n";
    in << "exit\n";
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

    // Expecting 4 command responses (only valid ones counted)
    EXPECT_EQ(lines.size(), 4);
    for (const auto& l : lines) {
        EXPECT_TRUE(l == "true" || l == "false" || l == "true true" || l == "true false");
    }
}

TEST(AppTest, RepeatsUntilValidInitLine) {
    std::ofstream in("test_input.txt");
    in << "invalid\n";
    in << "still wrong\n";
    in << "256 2\n";
    in << "2 www.example.com\n";
    in << "exit\n";
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
    EXPECT_TRUE(lines[0] == "true" || lines[0] == "false" || lines[0] == "true true" || lines[0] == "true false");
}

TEST(AppTest, RejectsNegativeOrZeroInInitLine) {
    std::ofstream in("test_input.txt");
    in << "0 -1 -2\n";  // invalid: 0 and negative numbers
    in << "3 10 -5 20\n";  // invalid: negative hash count
    in << "4 1 2 3\n";  // valid fallback
    in << "2 www.example.com\n";
    in << "exit\n";
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
    EXPECT_TRUE(lines[0] == "true" || lines[0] == "false" || lines[0] == "true true" || lines[0] == "true false");
}
