#include <gtest/gtest.h>
#include <fstream>
#include <cstdio> 
#include "Params.h"

using namespace std;

TEST(ParamsTest, NoExistingFile_NewFileShouldBeTrue) {
    remove("params.txt"); 

    int array[2] = {8, 2};
    Params params(8, array);

    EXPECT_TRUE(params.getNewFile());
}

TEST(ParamsTest, FileExists_NewFileShouldBeFalse) {
    int array1[2] = {8, 2};
    Params firstParams(128, array1);

    // Same config
    int array2[2] = {8, 2}; 
    Params secondParams(8, configArray2);

    EXPECT_FALSE(secondParams.getNewFile());
}

TEST(ParamsTest, InitCreatesFileIfNotExists) {
    remove("params.txt"); // Delete if exists

    int configArray[2] = {8, 2};
    Params params(8, configArray);

    ifstream file("params.txt");
    EXPECT_TRUE(file.is_open()); // File should now exist
    file.close();
}