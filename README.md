#  URL Blacklist Filter Application

##  About the Project
This project implements a URL filtering system using a Bloom Filter and TCP socket communication.
The application allows users to add URLs to a blacklist, check if URLs are blacklisted, or remove URLs from the blacklist by sending string-based commands via a TCP client.

The server continuously listens for commands over TCP sockets:


- **POST**: Add a URL to the blacklist.

- **GET**: Check if a URL is blacklisted.

- **DELETE**: Remove a URL from the blacklist.

Both the Bloom Filter and the blacklist are persistently saved to disk after every update and automatically loaded upon server startup.

---

###  What is a Bloom Filter?
A Bloom Filter is a space-efficient probabilistic data structure used to test whether an element is a member of a set.  
It allows for false positives but guarantees no false negatives, making it ideal for quick membership checks with limited memory.

---

##  Technologies Used
- C++17
- CMake
- GoogleTest (GTest) for unit testing
- Docker
- TCP/IP socket programming

---

##  Diagrams and Process Flows

### 1. Bloom Filter Structure

<p align="center">
  <img src="images/bloom_filter_structure.png" alt="Bloom Filter Structure" width="350"/>
</p>

**Description:**  
This diagram illustrates how a key (such as a URL) is processed by multiple hash functions, each mapping to an index in the bit array.  
The corresponding bits are set to `1` to indicate that the key has been "inserted" into the Bloom Filter.

---

### 2. URL Check Logic Flowchart

<p align="center">
  <img src="images/url_check_flowchart.png" alt="URL Check Flowchart" height="350"/>
</p>

**Description:**  
This flowchart illustrates the process of checking whether a URL is blacklisted:  
First, the hash is computed and checked in the Bloom Filter.  
If the Bloom Filter indicates a possible match, the real blacklist is checked for confirmation.

---

##  Build and Run Instructions
All components (server, client, and tests) are managed using Docker Compose.
From the project root directory, open a terminal and run:

### Build All Services

```
docker-compose build
```
This command builds all services: server, client, and tests.

### Run the Server
_See the section "Server Command-Line Arguments" below for a full explanation of the arguments._

```
docker-compose run server [PORT] [ARRAY_SIZE] [repeat count for hash function 1] [repeat count for hash function 2] ...
```

### Run the Client
_See the section "Client Command-Line Arguments" below for a full explanation of the arguments._

```
docker-compose run client [SERVER_IP] [PORT]
```

### Run the Tests

```
docker-compose run --rm tests
```

##  Command Guide

| Client Command               | Server Response                  | Description                                                              |
|-----------------------------|----------------------------------|--------------------------------------------------------------------------|
| `POST www.url1.com`         | `201 Created`                    | Adds a new URL to the blacklist                                          |
| `GET www.url1.com`          | `200 Ok`<br><br>`true true`           | URL is in both Bloom Filter and blacklist (definite match)              |
| `GET www.url2.com`          | `200 Ok`<br><br>`false`          | URL is neither in Bloom Filter nor in blacklist                         |
| `GET www.falsepositive.com` | `200 Ok`<br><br>`true false`           | False positive: in Bloom Filter but not in blacklist                    |
| `DELETE www.falsepositive.com` | `404 Not Found`              | Cannot delete a URL that was never added to the blacklist               |
| `DELETE www.url1.com`       | `204 No Content`                 | Successfully removed the URL from the blacklist                         |
| `BADCOMMAND something`      | `400 Bad Request`                | Invalid command syntax                                                   |

---

## Server Command-Line Arguments

When running the server container, the following arguments must be provided:
```
[PORT] [ARRAY_SIZE] [HASH_CONFIG...]
```
- **PORT**: The TCP port the server listens on.
- **ARRAY_SIZE**: The size of the Bloom Filter's bit array (in bits).
- **HASH_CONFIG**: A list of integers specifying how many times each hash function is applied.

The number of values in HASH_CONFIG determines how many hash functions are used.

### Example 1
```
8080 100 1
```
- Port: 8080  
- Bloom Filter size: 100 bits  
- One hash function, applied once

### Example 2
```
8080 256 2 1
```
- Port: 8080  
- Bloom Filter size: 256 bits  
- Two hash functions:  
  - First applied twice  
  - Second applied once


## Client Command-Line Arguments

When running the client container, the following arguments must be provided:
```
[SERVER_IP] [PORT]
```

- **SERVER_IP**: The IP address of the server to connect to (e.g., `127.0.0.1` for localhost).
- **PORT**: The TCP port number the server is listening on.

### Example
```
127.0.0.1 8080
```
- Connects the client to the server at IP `127.0.0.1` and port `8080`.

---

##  Notes
- Both the Bloom Filter and the blacklist are saved to disk after every update.
- On startup, the program automatically loads the previously saved Bloom Filter and blacklist, **only if** the parameters specified in the first input line match the previous configuration.
- Invalid input lines are ignored.
- The program automatically handles false positives by checking the real blacklist when needed.
- False positives may occur, but false negatives are not possible.

---

##  Development Approach
- The project was developed using the principles of Test-Driven Development (TDD).
- The code design follows SOLID principles.
- The project architecture is based on Object-Oriented Programming (OOP) concepts to ensure modularity, scalability, and ease of maintenance.

---