#include <gtest/gtest.h>
#include "../src/server/Server.h"

class ServerTest : public ::testing::Test {
protected:
    int testPort = 1234;
    size_t arraySize = 8;
    std::vector<int> hashRepeats = {1, 2};

    Server* server;

    void SetUp() override {
        server = new Server(testPort, arraySize, hashRepeats);
    }

    void TearDown() override {
        delete server;
    }
};

TEST_F(ServerTest, CanCreateServerInstance) {
    ASSERT_NO_THROW({
        Server localServer(testPort, arraySize, hashRepeats);
    });
}