; ModuleID = 'in.c'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128"
target triple = "x86_64-apple-darwin10.0"
	%struct.__sFILE = type <{ i8*, i32, i32, i16, i16, i8, i8, i8, i8, %struct.__sbuf, i32, i8, i8, i8, i8, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i8, i8, i8, i8, i64 }>
	%struct.__sFILEX = type opaque
	%struct.__sbuf = type <{ i8*, i32, i8, i8, i8, i8 }>
	%struct.class_IO = type opaque
	%struct.obj_IO = type <{ %struct.class_IO* }>
@__stderrp = external global %struct.__sFILE*		; <%struct.__sFILE**> [#uses=2]
@"\01LC" = internal constant [3 x i8] c"%s\00"		; <[3 x i8]*> [#uses=1]
@"\01LC1" = internal constant [38 x i8] c"Error: input cannot exceed 1000 chars\00"		; <[38 x i8]*> [#uses=1]

define void @IO_abort(%struct.obj_IO* %this, i8* %message) nounwind {
	%this.addr = alloca %struct.obj_IO*		; <%struct.obj_IO**> [#uses=1]
	%message.addr = alloca i8*		; <i8**> [#uses=2]
	store %struct.obj_IO* %this, %struct.obj_IO** %this.addr
	store i8* %message, i8** %message.addr
	%1 = load %struct.__sFILE** @__stderrp		; <%struct.__sFILE*> [#uses=1]
	%2 = load i8** %message.addr		; <i8*> [#uses=1]
	%3 = call i32 (%struct.__sFILE*, i8*, ...)* @fprintf(%struct.__sFILE* %1, i8* getelementptr ([3 x i8]* @"\01LC", i32 0, i32 0), i8* %2)		; <i32> [#uses=0]
	%4 = call i32 (...)* @exit(i32 1)		; <i32> [#uses=0]
	ret void
}

declare i32 @fprintf(%struct.__sFILE*, i8*, ...)

declare i32 @exit(...)

define %struct.obj_IO* @method_out(%struct.obj_IO* %this, i8* %message) nounwind {
	%1 = alloca %struct.obj_IO*		; <%struct.obj_IO**> [#uses=2]
	%this.addr = alloca %struct.obj_IO*		; <%struct.obj_IO**> [#uses=2]
	%message.addr = alloca i8*		; <i8**> [#uses=2]
	store %struct.obj_IO* %this, %struct.obj_IO** %this.addr
	store i8* %message, i8** %message.addr
	%2 = load i8** %message.addr		; <i8*> [#uses=1]
	%3 = call i32 (i8*, ...)* @printf(i8* getelementptr ([3 x i8]* @"\01LC", i32 0, i32 0), i8* %2)		; <i32> [#uses=0]
	%4 = load %struct.obj_IO** %this.addr		; <%struct.obj_IO*> [#uses=1]
	store %struct.obj_IO* %4, %struct.obj_IO** %1
	%5 = load %struct.obj_IO** %1		; <%struct.obj_IO*> [#uses=1]
	ret %struct.obj_IO* %5
}

declare i32 @printf(i8*, ...)

define i8* @IO_in(%struct.obj_IO* %this) nounwind {
	%1 = alloca i8*		; <i8**> [#uses=2]
	%this.addr = alloca %struct.obj_IO*		; <%struct.obj_IO**> [#uses=1]
	%numchar = alloca i32, align 4		; <i32*> [#uses=6]
	%c = alloca i8, align 1		; <i8*> [#uses=2]
	%in = alloca i8*, align 8		; <i8**> [#uses=2]
	%tmp = alloca [1000 x i8], align 1		; <[1000 x i8]*> [#uses=3]
	store %struct.obj_IO* %this, %struct.obj_IO** %this.addr
	store i32 0, i32* %numchar
	br label %2

; <label>:2		; preds = %21, %0
	%3 = call i32 @getchar()		; <i32> [#uses=1]
	%4 = trunc i32 %3 to i8		; <i8> [#uses=2]
	store i8 %4, i8* %c
	%5 = sext i8 %4 to i32		; <i32> [#uses=1]
	%6 = icmp ne i32 %5, 10		; <i1> [#uses=1]
	br i1 %6, label %7, label %22

; <label>:7		; preds = %2
	%8 = load i8* %c		; <i8> [#uses=1]
	%9 = load i32* %numchar		; <i32> [#uses=1]
	%10 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0		; <i8*> [#uses=1]
	%11 = sext i32 %9 to i64		; <i64> [#uses=1]
	%12 = getelementptr i8* %10, i64 %11		; <i8*> [#uses=1]
	store i8 %8, i8* %12
	%13 = load i32* %numchar		; <i32> [#uses=1]
	%14 = add i32 %13, 1		; <i32> [#uses=1]
	store i32 %14, i32* %numchar
	%15 = load i32* %numchar		; <i32> [#uses=1]
	%16 = icmp sge i32 %15, 1000		; <i1> [#uses=1]
	br i1 %16, label %17, label %21

; <label>:17		; preds = %7
	%18 = load %struct.__sFILE** @__stderrp		; <%struct.__sFILE*> [#uses=1]
	%19 = call i32 (%struct.__sFILE*, i8*, ...)* @fprintf(%struct.__sFILE* %18, i8* getelementptr ([38 x i8]* @"\01LC1", i32 0, i32 0))		; <i32> [#uses=0]
	%20 = call i32 (...)* @exit(i32 1)		; <i32> [#uses=0]
	br label %21

; <label>:21		; preds = %17, %7
	br label %2

; <label>:22		; preds = %2
	%23 = load i32* %numchar		; <i32> [#uses=1]
	%24 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0		; <i8*> [#uses=1]
	%25 = sext i32 %23 to i64		; <i64> [#uses=1]
	%26 = getelementptr i8* %24, i64 %25		; <i8*> [#uses=1]
	store i8 0, i8* %26
	%27 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0		; <i8*> [#uses=1]
	store i8* %27, i8** %in
	%28 = load i8** %in		; <i8*> [#uses=1]
	store i8* %28, i8** %1
	%29 = load i8** %1		; <i8*> [#uses=1]
	ret i8* %29
}

declare i32 @getchar()

define i32 @main(i32 %junk) nounwind {
	%1 = alloca i32		; <i32*> [#uses=2]
	%junk.addr = alloca i32		; <i32*> [#uses=1]
	store i32 %junk, i32* %junk.addr
	store i32 0, i32* %1
	%2 = load i32* %1		; <i32> [#uses=1]
	ret i32 %2
}
