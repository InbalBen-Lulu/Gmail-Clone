const net = require('net');

// Blacklist server connection details
const BLACKLIST_SERVER_IP = process.env.BLACKLIST_SERVER_IP;
const BLACKLIST_SERVER_PORT = process.env.BLACKLIST_SERVER_PORT;

/**
 * Opens a new TCP connection to the blacklist server,
 * sends a command, and returns the server's raw response.
 * Each call opens and closes its own socket.
 * @param {string} message - The command to send (e.g., "GET url")
 * @returns {Promise<string>} - The raw response string
 */
function sendToBlacklistServer(message) {
    return new Promise(function (resolve, reject) {
        const client = new net.Socket();

        client.connect(BLACKLIST_SERVER_PORT, BLACKLIST_SERVER_IP, function () {
            client.write(message + '\n'); // Ensure newline termination
        });

        client.on('data', function (data) {
            resolve(data.toString().trim());
            client.destroy();
        });

        client.on('error', function (err) {
            reject(err);
        });

        client.on('close', function () {
            // Connection closed
        });
    });
}

/**
 * Checks if any of the provided words are blacklisted.
 * The function sends all GET requests in parallel and returns true
 * immediately when the first "true true" is received.
 *
 * @param {string[]} words - An array of words (URLs) to check.
 * @returns {Promise<boolean>} - Resolves to true if at least one word is blacklisted, otherwise false.
 */
async function checkUrlsAgainstBlacklist(words) {
  return new Promise(function (resolve, reject) {
    let pending = words.length; // How many requests are still pending
    let found = false; // Whether we already found a blacklisted word

    for (let i = 0; i < words.length; i++) {
      const word = words[i];

      sendToBlacklistServer('GET ' + word)
        .then(function (response) {
          // Ignore response if we already found a blacklisted word
          if (found) return;

          const lines = response.split('\n\n');

          // If the response indicates the word is blacklisted ("true true")
          if (
            lines[0]?.startsWith('200 Ok') &&
            lines.at(-1)?.trim() === 'true true'
          ) {
            found = true;
            return resolve(true); // Exit early — at least one word is blacklisted
          }

          // If not blacklisted, update pending counter
          pending--;
          if (pending === 0 && !found) {
            resolve(false); // All words checked — none are blacklisted
          }
        })
        .catch(function (err) {
          // If a request fails, treat it as non-blacklisted
          pending--;
          if (pending === 0 && !found) {
            resolve(false);
          }
        });
    }
  });
}

/**
 * Adds a URL to the blacklist.
 * Expects response: "201 Created"
 * @param {string} word
 * @returns {Promise<{ success: boolean, message: string }>}
 */
async function addToBlacklist(word) {
    const response = await sendToBlacklistServer('POST ' + word);
    if (response === '201 Created') {
        return { success: true, message: 'URL added to blacklist' };
    }
    return { success: false, message: 'Failed to add URL (no response)' };
}

/**
 * Removes a URL from the blacklist.
 * Handles multiple types of server responses.
 * @param {string} word
 * @returns {Promise<{ success: boolean, message: string }>}
 */
async function removeFromBlacklist(word) {
    const response = await sendToBlacklistServer('DELETE ' + word);

    if (response === '204 No Content') {
        return { success: true, message: 'URL removed from blacklist' };
    } else if (response === '404 Not Found') {
        return { success: false, message: 'URL not found in blacklist' };
    } else if (response === '400 Bad Request') {
        return { success: false, message: 'Invalid DELETE request' };
    }
    return { success: false, message: 'Unexpected response' };
}

/**
 * Adds multiple URLs to the blacklist.
 * Ignores failures silently.
 * @param {string[]} words
 */
async function addUrlsToBlacklist(words) {
    const decodedWords = words.map(word => {
        try {
            return decodeURIComponent(word);
        } catch (e) {
            return word;
        }
    });
    await Promise.all(decodedWords.map(word => addToBlacklist(word)));
}

/**
 * Removes multiple URLs from the blacklist.
 * Ignores failures silently.
 * @param {string[]} words
 */
async function removeUrlsFromBlacklist(words) {
    const decodedWords = words.map(word => {
        try {
            return decodeURIComponent(word);
        } catch (e) {
            return word;
        }
    });
    await Promise.all(decodedWords.map(word => removeFromBlacklist(word)));
}

module.exports = {
    checkUrlsAgainstBlacklist,
    addToBlacklist,
    removeFromBlacklist,
    addUrlsToBlacklist,
    removeUrlsFromBlacklist
};