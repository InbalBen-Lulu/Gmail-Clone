# קובצי מקור לאפליקציה
file(GLOB_RECURSE APP_SOURCES src/*.cpp)
list(REMOVE_ITEM APP_SOURCES ${CMAKE_SOURCE_DIR}/src/app/main.cpp)

# קובצי טסט
file(GLOB_RECURSE TEST_SOURCES tests/*.cpp)

# בנה את הטסטים בלבד
add_executable(runTests ${APP_SOURCES} ${TEST_SOURCES})

target_link_libraries(runTests gtest gtest_main pthread)
