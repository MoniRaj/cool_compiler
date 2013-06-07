; ModuleID = 'driver.c'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128"
target triple = "x86_64-apple-darwin10.0"
@"\01LC" = internal constant [19 x i8] c"Calling llvm main\0A\00"		; <[19 x i8]*> [#uses=1]
@"\01LC1" = internal constant [27 x i8] c"Survived it, value is %d \0A\00"		; <[27 x i8]*> [#uses=1]

define i32 @main(i32 %argc, i8** %argv) nounwind {
	%1 = alloca i32		; <i32*> [#uses=1]
	%argc.addr = alloca i32		; <i32*> [#uses=1]
	%argv.addr = alloca i8**		; <i8***> [#uses=1]
	%result = alloca i32, align 4		; <i32*> [#uses=2]
	store i32 %argc, i32* %argc.addr
	store i8** %argv, i8*** %argv.addr
	%2 = call i32 (i8*, ...)* @printf(i8* getelementptr ([19 x i8]* @"\01LC", i32 0, i32 0))		; <i32> [#uses=0]
	%3 = call i32 @ll_main(i32 42)		; <i32> [#uses=1]
	store i32 %3, i32* %result
	%4 = load i32* %result		; <i32> [#uses=1]
	%5 = call i32 (i8*, ...)* @printf(i8* getelementptr ([27 x i8]* @"\01LC1", i32 0, i32 0), i32 %4)		; <i32> [#uses=0]
	%6 = load i32* %1		; <i32> [#uses=1]
	ret i32 %6
}

declare i32 @printf(i8*, ...)

declare i32 @ll_main(i32)
