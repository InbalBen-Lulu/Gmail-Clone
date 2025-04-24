#include <gtest/gtest.h>
#include <fstream>
#include <cstdio> 
#include "../src/storage/Params.h"

using namespace std;

TEST(ParamsTest, NoExistingFile_NewFileShouldBeTrue) {
    remove("../data/params.txt"); 

    int array[2] = {8, 2};
    Params params(8, array);

    EXPECT_TRUE(params.getNewFile());
}

TEST(ParamsTest, FileExists_NewFileShouldBeFalse) {
    int array1[2] = {8, 2};
    Params firstParams(8, array1);

    // Same config
    int array2[2] = {8, 2}; 
    Params secondParams(8, array2);

    EXPECT_FALSE(secondParams.getNewFile());
}

TEST(ParamsTest, InitCreatesFileIfNotExists) {
    remove("../data/params.txt"); // Delete if exists

    int configArray[2] = {8, 2};
    Params params(8, configArray);

    ifstream file("../data/params.txt");
    EXPECT_TRUE(file.is_open()); // File should now exist
    file.close();
}