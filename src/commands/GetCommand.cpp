#include "GetCommand.h"

// Constructor: initializes GetCommand with references to BloomFilter and BlackList
GetCommand::GetCommand(BloomFilter& bloom, BlackList& bl)
    : bloomFilter(bloom), blackList(bl) {}

/*
 * Executes the GetCommand:
 * - Hashes the given URL
 * - Checks if all required bits are set in the BloomFilter
 * - If not all bits are set, returns "200 OK\n\nfalse"
 * - If all bits are set:
 *      - Checks if the URL is in the BlackList
 *      - Returns "200 OK\n\ntrue true" or "200 OK\n\ntrue false" accordingly
 */
std::string GetCommand::execute(const Url& url, Hash& hash) {
    std::string response = "200 Ok\n\n";
    
    std::vector<int> hashBits = hash.execute(url); 
    bool allBitsOn = bloomFilter.contain(hashBits);

    if (!allBitsOn) {
        response += "false";
    } else {
        bool urlInBlackList = blackList.contains(url);
        response += "true " + std::string(urlInBlackList ? "true" : "false");
    }

    return response;
}