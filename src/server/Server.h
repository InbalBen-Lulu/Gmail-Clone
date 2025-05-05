#pragma once
#include <memory>
#include <map>
#include <vector>
#include <string>
#include <cstring>
#include "../data/BloomFilter.h"
#include "../storage/BloomStorage.h"
#include "../data/BlackList.h"
#include "../storage/BlackListStorage.h"
#include "../commands/ICommand.h"
#include "../utils/Hash.h"
#include "../parser/InputParser.h"
#include "../storage/Params.h"


/**
 * @class Server
 * @brief The Server class manages a TCP server that handles commands for a Bloom Filter and a Blacklist.
 * 
 * It receives commands from a client (e.g., POST, GET, DELETE), parses them, and executes 
 * the appropriate logic using data structures and persistent storage. The server uses a 
 * BloomFilter for efficient probabilistic lookups and a BlackList for exact matches.
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
     * @brief Initializes the server's socket and begins listening for connections.
     */
    void setup();

    /**
     * @brief Handles client requests after a connection is established.
     * 
     * @param clientSocket The socket file descriptor for the connected client.
     */
    void handleClient(int clientSocket);

    /**
     * @brief Initializes the system components such as BloomFilter, BlackList, storage, and hash logic.
     * 
     * @param arraySize The size of the BloomFilter's internal bit array.
     * @param hashArray A vector indicating how many times each hash function should be applied.
     */
    void initSystem(size_t arraySize, const std::vector<int>& hashArray);

    /**
     * @brief Registers all available command handlers.
     */
    void registerCommands();

public:
    int serverSocket;

    /**
     * @brief Constructs the server and initializes its subsystems.
     * 
     * @param port The TCP port the server should bind to.
     * @param arraySize The BloomFilter bit array size.
     * @param hashArray Hash configuration for the Hash object.
     */
    Server(int port, size_t arraySize, std::vector<int>& hashArray);
    
     /**
     * @brief Runs the server: sets up the socket, waits for a client, and processes its requests.
     */
    void run();
};