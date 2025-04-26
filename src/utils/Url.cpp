#include "Url.h"
#include <regex>

Url::Url(const std::string& url) : urlPath(url) {}

bool Url::isValid() const {
    static const std::regex urlRegex(
        R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)",
        std::regex::icase
    );
    return std::regex_match(urlPath, urlRegex);
}

std::string Url::getUrlPath() const {
    return urlPath;
}

bool Url::operator<(const Url& other) const {
    return urlPath < other.urlPath;
}
