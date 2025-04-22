#include "Params.h"

Params::Params(int arraySize, int* configArray)
    : arraySize(arraySize), newFile(true) {
    // TODO: Store configArray into member variable if needed
}

void Params::init() {
    // TODO: Initialize parameters from file or default
}

void Params::load() {
    // TODO: Load parameters from file
}

bool Params::getNewFile() const {
    return newFile;
}
