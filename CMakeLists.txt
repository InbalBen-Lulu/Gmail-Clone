cmake_minimum_required(VERSION 3.10)

project(MyExample)

set(CMAKE_CXX_STANDARD 11)

include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/refs/tags/release-1.11.0.zip
)

# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

# run everything in tests
# file(GLOB TEST_SOURCES "tests/*.cpp")
# file(GLOB SRC_SOURCES "src/*.cpp")
# add_executable(runTests ${SRC_SOURCES} ${TEST_SOURCES})

#run one test
add_executable(runTests add_url_test.cpp)

target_link_libraries(runTests gtest_main)
