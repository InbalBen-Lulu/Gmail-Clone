#include "Url.h"

Url::Url(const std::string& url) : urlPath(url) {}

bool Url::isValid() const {
    // TODO: Add URL validation logic
    return !urlPath.empty();
}

std::string Url::getUrlPath() const {
    return urlPath;
}

bool Url::operator<(const Url& other) const {
    return urlPath < other.urlPath;
}
