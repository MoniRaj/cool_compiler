#! /bin/sh
#
#  Compile C to llvm assembly language
#
clang $1 -S -emit-llvm 

