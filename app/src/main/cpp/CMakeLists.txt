cmake_minimum_required(VERSION 3.10.2)

project("emma")

file(GLOB srcs ${CMAKE_SOURCE_DIR}/*.c ${CMAKE_SOURCE_DIR}/*.cpp )
file(GLOB hdrs ${CMAKE_SOURCE_DIR}/*.h ${CMAKE_SOURCE_DIR}/*.hpp )

add_library(emma SHARED
        ${hdrs}
        ${srcs}
        )

find_library(log-lib log )
target_link_libraries( emma ${log-lib} )
