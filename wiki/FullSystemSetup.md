# MailMe – Mail System

- [Run the project](#running-the-project)
- [Spam filtering logic](#spam-filtering)

## Related Guides:

- [ Web Client Instructions](WebClient.md) — how to use the React-based MailMe web app.
- [ Android Client Instructions](AndroidClient.md) — how to build and run the Android application.

## Overview

**MailMe** is a full-stack email platform with a modern UI and built-in security. It combines:

- A **C++ URL filtering server** that prevents sending messages with blacklisted URLs using a **Bloom Filter**.
- A **Node.js API** for managing users, mails, labels and authentication.
- A **React-based client** providing a Gmail-like user experience with support for drafts, labels, stars, spam, and more.
- A **native Android application** with full mail functionality: compose, edit, delete, labels, drafts, and real-time syncing with the server.

---

## Running the Project

Follow the steps below to run the full MailMe system locally using Docker:

### Build All Services

```
docker-compose build
```

### Create shared Docker network
```
docker network create mail_service
```

### Run the C++ blacklist server

```
docker rm -f server 2>$null
```
```
docker run --name server --network mail_service -w /usr/src/project/build -v "${PWD}/blacklist_service/data:/usr/src/project/build/data" project-server 8080 <ARRAY_SIZE> <hash1_repeat> <hash2_repeat> ...
```


## Next Steps

Now that the full system is up and running, you can continue with:

- [Web Client Usage Guide](WebClient.md)  
  Learn how to register, compose, label, and manage mails using the React web app.

- [Android App Usage Guide](AndroidClient.md)  
  Learn how to build and use the native Android client to manage your emails on mobile.

---

### Run the Tests

```
docker-compose run --rm tests
```

## Spam Filtering

When a user marks a mail as **spam**, all URLs within that mail are extracted and added to the **blacklist** using a **C++ server with Bloom Filter** logic.

From that moment on, **any future mail containing any of these URLs will be automatically classified as spam for all users**, even before reaching their inbox.

This mechanism provides a collaborative spam defense system:
- A single spam report strengthens protection for everyone.
- Thanks to the Bloom Filter, URL checks are extremely fast and space-efficient.

If a user later **removes a mail from spam**, the system performs the reverse action:  
All URLs extracted from that mail are removed from the blacklist, and future messages containing those URLs will no longer be flagged as spam.
