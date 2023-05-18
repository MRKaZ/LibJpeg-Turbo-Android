# Copyright 2023 Kasun Gamage
#
# [LICENSE]
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Copyright (c) 2023 Kasun Gamage All rights reserved.

cmake_minimum_required(VERSION 3.22.1)

project("jpeg-turbo")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

add_library(
        turbojpeg-jni
        SHARED
        ${CMAKE_SOURCE_DIR}/turbojpeg-jni.c
)

add_library(
        jpeg
        SHARED
        IMPORTED
)

set_target_properties(
        jpeg
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libjpeg.so
)

add_library(
        jpeg-turbo
        SHARED
        IMPORTED
)

set_target_properties(
        jpeg-turbo
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libturbojpeg.so
)

include_directories(${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/includes)
include_directories(${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/java)

target_link_libraries(
        turbojpeg-jni
        jpeg
        jpeg-turbo
        log
)
