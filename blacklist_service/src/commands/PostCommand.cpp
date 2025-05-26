#include "PostCommand.h"
#include <mutex>

// Constructor: initializes PostCommand with references to a BloomFilter and a BlackList
PostCommand::PostCommand(BloomFilter& bloom, BlackList& bl, std::mutex& m)
    : bloomFilter(bloom), blackList(bl), mutex(m) {}

/*
 * Executes the PostCommand:
 * - Hashes the given URL
 * - Adds the resulting hash bits to the BloomFilter
 * - Adds the URL to the BlackList
 * - Returns "201 Created" upon successful addition
 */
std::string PostCommand::execute(const Url& url, Hash& hash) {
    std::lock_guard<std::mutex> lock(mutex);

    std::vector<int> hashResult = hash.execute(url); // Generate hash bits for the URL
    bloomFilter.add(hashResult);
    blackList.add(url);
    return "201 Created";
}
