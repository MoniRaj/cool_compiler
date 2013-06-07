#
# Makefile with recipes for using LLVM tools
#

#  Tool paths: configure this depending on where the LLVM tools live
#
LLVM = $(HOME)/Tools/clang+llvm-3.2-x86_64-apple-darwin11/bin



## From llvm code to architecture specific assembly code as *.s
##
%.s:	%.ll
	$(LLVM)/llc $< 

## From c code to llvm code 
##
%.ll:	%.c
	$(LLVM)/clang -S -emit-llvm $< -o - > $@


##
##  OO Dispatch example
##
dispatch:	dispatch.ll
	$(LLVM)/clang driver.c $< -o $@

##
##  OO Dispatch example in hand-written llvm code 
##
pair:	pair.ll
	$(LLVM)/clang driver.c $< -o $@



