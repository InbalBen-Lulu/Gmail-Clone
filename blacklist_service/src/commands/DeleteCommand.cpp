#include "DeleteCommand.h"
#include <mutex>

// Constructor: initializes DeleteCommand with references to BloomFilter and BlackList
DeleteCommand::DeleteCommand(BloomFilter& bloom, BlackList& bl, std::mutex& m)
    : bloomFilter(bloom), blackList(bl), mutex(m) {}

/*
 * Executes the DeleteCommand:
 * - Checks if the URL exists in the BlackList
 * - If it exists: deletes it from the BlackList and returns "204 No Content"
 * - If it doesn't exist: returns "404 Not Found"
 */
std::string DeleteCommand::execute(const Url& url, Hash& hash) {
    std::lock_guard<std::mutex> lock(mutex);

    if (blackList.contains(url)) {
        blackList.deleteUrl(url);
        return "204 No Content";
    } else {
        return "404 Not Found";
    }
}
