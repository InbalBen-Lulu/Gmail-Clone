#pragma once
#include <string>

class Url {
private:
    std::string urlPath;
public:
    Url(const std::string&);
    bool isValid() const;
    std::string getUrlPath() const;
    bool operator<(const Url& other) const;
};
