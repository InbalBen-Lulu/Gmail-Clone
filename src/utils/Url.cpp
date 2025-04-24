#include "Url.h"

Url::Url(const std::string& url) : urlPath(url) {}

bool Url::isValid() const {
    // TODO: Add URL validation logic
    //return !urlPath.empty();
    return false;
}

std::string Url::getUrlPath() const {
    // return urlPath;
    return "";
}

bool Url::operator<(const Url& other) const {
    //return urlPath < other.urlPath;
    return false;
}
