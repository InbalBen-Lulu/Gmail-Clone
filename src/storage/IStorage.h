#pragma once

class IStorage {
public:
    virtual void init() = 0;
    virtual void load() = 0;
    virtual ~IStorage() = default;
};
