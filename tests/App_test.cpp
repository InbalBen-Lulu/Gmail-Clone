#include <gtest/gtest.h>
#include <sstream>
#include <string>
#include <vector>
#include "../src/app/App.h"

// ===================== APP::CONSTRUCTION TEST =====================

TEST(AppTest, ConstructorInitializesWithoutException) {
    EXPECT_NO_THROW({
        App app;
    });
}

// ===================== APP::RUN TESTS =====================

// Helper to simulate input
void simulateInput(const std::string& input) {
    static std::istringstream inputBuffer;
    inputBuffer.str(input);
    inputBuffer.clear();
    std::cin.rdbuf(inputBuffer.rdbuf());
}

TEST(AppTest, ValidInitAndSimpleCommands) {
    // Simulate user input
    simulateInput(
        "128 1 2\n"
        "1 www.example.com0\n"
        "2 www.example.com0\n"
        "2 www.example.com123\n"
    );

    // Capture output
    testing::internal::CaptureStdout();
    
    App app;
    app.run();
    
    std::string output = testing::internal::GetCapturedStdout();

    std::cout << "=== Captured output ===\n" << output << "\n=======================\n";

    // Split output into lines
    std::istringstream outputStream(output);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(outputStream, line)) {
        if (!line.empty()) {
            lines.push_back(line);
        }
    }

    // Verify
    ASSERT_GE(lines.size(), 2) << "[ERROR] Less than 2 output lines!";
    EXPECT_EQ(lines[0], "true true");
    EXPECT_TRUE(lines[1] == "false" || lines[1] == "true false");
}

TEST(AppTest, InitAndInvalidCommand) {
    simulateInput(
        "a\n"
        "8 1 2\n"
        "2 www.example.com0\n"
        "x\n"
    );

    testing::internal::CaptureStdout();
    
    App app;
    app.run();
    
    std::string output = testing::internal::GetCapturedStdout();

    std::istringstream outputStream(output);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(outputStream, line)) {
        if (!line.empty()) {
            lines.push_back(line);
        }
    }

    ASSERT_EQ(lines.size(), 1);
    EXPECT_EQ(lines[0], "false");
}

TEST(AppTest, CommandsOnExampleComVariants) {
    simulateInput(
        "128 1 2\n"
        "1 www.example.com0\n"
        "2 www.example.com0\n"
        "2 www.example.com1\n"
        "2 www.example.com11\n"
    );

    testing::internal::CaptureStdout();
    
    App app;
    app.run();
    
    std::string output = testing::internal::GetCapturedStdout();

    std::istringstream outputStream(output);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(outputStream, line)) {
        if (!line.empty()) {
            lines.push_back(line);
        }
    }

    EXPECT_EQ(lines.size(), 3);
    EXPECT_EQ(lines[0], "true true");
    for (size_t i = 1; i < lines.size(); ++i) {
        EXPECT_TRUE(lines[i] == "false" || lines[i] == "true false");
    }
}

TEST(AppTest, RepeatsUntilValidInitLine) {
    simulateInput(
        "invalid\n"
        "still wrong\n"
        "256 2\n"
        "2 www.example.com\n"
    );

    testing::internal::CaptureStdout();
    
    App app;
    app.run();
    
    std::string output = testing::internal::GetCapturedStdout();

    std::istringstream outputStream(output);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(outputStream, line)) {
        if (!line.empty()) {
            lines.push_back(line);
        }
    }

    EXPECT_EQ(lines.size(), 1);
    EXPECT_TRUE(lines[0] == "false");
}

TEST(AppTest, RejectsNegativeOrZeroInInitLine) {
    simulateInput(
        "0 -1 -2\n"
        "3 10 -5 20\n"
        "4 1 2 3\n"
        "2 www.example.com\n"
    );

    testing::internal::CaptureStdout();
    
    App app;
    app.run();
    
    std::string output = testing::internal::GetCapturedStdout();

    std::istringstream outputStream(output);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(outputStream, line)) {
        if (!line.empty()) {
            lines.push_back(line);
        }
    }

    EXPECT_EQ(lines.size(), 1);
    EXPECT_TRUE(lines[0] == "false");
}
