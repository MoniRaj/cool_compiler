/*
 * A simple driver program to drive an llvm program.  Here we build a "main" that in turn 
 * calls a "ll_main" function in llvm code. 
 */

#include <stdio.h>

extern int ll_main(int arg);

int main(int argc, char **argv) {
  int result;
  printf("Calling llvm main\n");
  result = ll_main(42);
  printf("Survived it, value is %d \n", result);
}

