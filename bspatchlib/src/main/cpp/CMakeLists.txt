cmake_minimum_required(VERSION 3.4.1)

# 查找文件系统中制定模式的路径，如：/*是匹配根目录下的所有文件
file(GLOB bzip_source ${CMAKE_SOURCE_DIR}/bzip/*.c)

# 设置本地动态库，编译生成动态库
add_library(
        bspatcher # 模块名

        SHARED # 动态库、分享库

        bspatcher.cpp
        bspatch.c
        ${bzip_source}) # 源文件

# 查找系统库，日志输出库Log
find_library(
        log-lib

        log)

# 需要链接或者编译的库
target_link_libraries(
        bspatcher

        ${log-lib})