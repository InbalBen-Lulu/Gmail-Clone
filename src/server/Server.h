#pragma once
#include <memory>
#include <map>
#include <vector>
#include <string>
#include "../data/BloomFilter.h"
#include "../storage/BloomStorage.h"
#include "../data/BlackList.h"
#include "../storage/BlackListStorage.h"
#include "../commands/ICommand.h"
#include "../utils/Hash.h"
#include "../parser/InputParser.h"

/**
 * Server class handles client connections, parses incoming commands,
 * and delegates them to the appropriate ICommand implementations.
 */
class Server {
private:
    std::unique_ptr<BloomFilter> bloomFilter;
    std::unique_ptr<BlackList> blackList;
    std::map<std::string, std::unique_ptr<ICommand>> commands;
    std::shared_ptr<Hash> hash;
    std::unique_ptr<BlackListStorage> blackListStorage;
    std::unique_ptr<BloomStorage> bloomStorage;
    int port;

    /**
     * Sets up the server socket: creates, binds, and starts listening.
     */
    void setup();

    /**
     * Handles communication with a connected client.
     * @param clientSocket The socket file descriptor for the client.
     */
    void handleClient(int clientSocket);

    /**
     * Initializes all system components: storage, data structures, and hashing.
     * @param arraySize The size of the bloom filter array in bits.
     * @param hashArray The configuration for how many times to apply each hash function.
     */
    void initSystem(size_t arraySize, const std::vector<int>& hashArray);

    /**
     * Registers all available commands and maps them to their string identifiers.
     */
    void registerCommands();

public:
    /**
     * Constructs the server with specified port, bloom filter size, and hash configuration.
     * @param port The TCP port to listen on.
     * @param arraySize The bloom filter size.
     * @param hashArray Vector indicating how many times each hash function should be applied.
     */
    Server(inr port, size_t arraySize, std::vector<int>& hashArray);

    /**
     * Starts the server: sets up socket and handles a single client session.
     */
    void run();
};