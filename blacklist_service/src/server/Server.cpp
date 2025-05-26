#include "Server.h"
#include "../commands/PostCommand.h"
#include "../commands/GetCommand.h"
#include "../commands/DeleteCommand.h"
#include <iostream>
#include <netinet/in.h>
#include <unistd.h>
#include <cstring>
#include <sstream>
#include <mutex>

// Constructor: initializes the system and registers available commands
Server::Server(int port, size_t arraySize, std::vector<int>& hashArray) : port(port) {
    initSystem(arraySize, hashArray);
    registerCommands();
}

// Initializes system components like Params, storages, filter, blacklist, and hash
void Server::initSystem(size_t arraySize, const std::vector<int>& hashArray) {
    Params params(arraySize, hashArray);
    bool isNewFile = params.getNewFile();

    // Initialize components based on whether new file is needed
    bloomStorage = std::make_unique<BloomStorage>(isNewFile);
    blackListStorage = std::make_unique<BlackListStorage>(isNewFile);
    bloomFilter = std::make_unique<BloomFilter>(*bloomStorage, arraySize);
    blackList = std::make_unique<BlackList>(*blackListStorage);
    hash = std::make_shared<Hash>(hashArray, arraySize);
}

// Registers the supported HTTP-like commands
void Server::registerCommands() {
    // Pass the same mutex instance to all commands to synchronize access to shared resources
    commands["POST"] = std::make_unique<PostCommand>(*bloomFilter, *blackList, commandMutex);
    commands["GET"] = std::make_unique<GetCommand>(*bloomFilter, *blackList, commandMutex);
    commands["DELETE"] = std::make_unique<DeleteCommand>(*bloomFilter, *blackList, commandMutex);
}

// Prepares the socket, binds it to the port, and starts listening for incoming connections
void Server::setup() {
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket < 0) {
        exit(1);
    }

    struct sockaddr_in sin{};
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;  // Accept connections on any interface
    sin.sin_port = htons(port); // Convert port number to network byte order

    if (bind(serverSocket, (struct sockaddr*)&sin, sizeof(sin)) < 0) {
        exit(1);
    }

    if (listen(serverSocket, 5) < 0) {
        exit(1);
    }
}

// Main loop: accepts a client connection, handles it, and then closes sockets
void Server::run() {
    setup();
    while (true){
        struct sockaddr_in client_sin;
        unsigned int addr_len = sizeof(client_sin);

        int clientSocket = accept(serverSocket, (struct sockaddr*)&client_sin, &addr_len);
        if (clientSocket < 0) {
            exit(1);
        }
        
        // Each client connection is handled in its own detached thread
        // This allows multiple clients to be served concurrently
        std::thread([this, clientSocket]() {
            this->handleClient(clientSocket);
            close(clientSocket);
        }).detach(); 
    }
}

// Handles requests from the connected client in a loop
void Server::handleClient(int clientSocket) {
    char buffer[4096];
    while (true) {

        int bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

        if (bytesRead == 0) {
            break;  
        }
        
        if (bytesRead < 0) {
            continue; 
        }

        buffer[bytesRead] = '\0';
        std::string line(buffer);

        // Parse the input into a command structure
        std::optional<CommandInput> maybeCmd = InputParser::parseCommandLine(line);
        if (!maybeCmd.has_value()) {
            std::string err = "400 Bad Request";
            send(clientSocket, err.c_str(), err.length(), 0);
            continue;
        }

        CommandInput cmd = maybeCmd.value();
        auto it = commands.find(cmd.command); // Look up the command
        if (it == commands.end()) {
            std::string err = "400 Bad Request";
            send(clientSocket, err.c_str(), err.length(), 0);
            continue;
        }
        // Execute the command and send back the response
        std::string response = it->second->execute(cmd.url, *hash);
        send(clientSocket, response.c_str(), response.length(), 0);
    }
}
