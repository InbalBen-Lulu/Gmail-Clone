cmake_minimum_required(VERSION 3.10)

project(MyExample)

set(CMAKE_CXX_STANDARD 17)

# Option to choose whether to build tests or the main application
option(BUILD_TESTING "Build tests" OFF)

# Fetch GoogleTest for unit testing
include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/refs/tags/release-1.11.0.zip
)

set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

<<<<<<< HEAD
# Collect all source files under src/
file(GLOB_RECURSE SRC_SOURCES 
    "src/*.cpp"
)

# Exclude the application main.cpp when building tests
list(FILTER SRC_SOURCES EXCLUDE REGEX ".*/app/main.cpp$")

# Collect all test files under tests/
file(GLOB TEST_SOURCES "tests/*.cpp")
set(TEST_MAIN_SRC "tests/main.cpp")

if(BUILD_TESTING)
    message(STATUS "Building tests...")

    # Exclude the tests' main.cpp from normal test files (will add it separately)
    list(REMOVE_ITEM TEST_SOURCES ${TEST_MAIN_SRC})

    # Create a single executable for all tests
    add_executable(runTests ${SRC_SOURCES} ${TEST_SOURCES} ${TEST_MAIN_SRC})

    # Link GoogleTest and pthreads
    target_link_libraries(runTests
        gtest_main
        pthread
    )

else()
    message(STATUS "Building main application...")

    # Build the main application with the real application main.cpp
    add_executable(app ${SRC_SOURCES} src/app/main.cpp)
endif()
