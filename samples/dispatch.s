; ModuleID = 'dispatch.c'
target datalayout = "e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128"
target triple = "x86_64-apple-darwin10.0"
	%struct.single_class = type <{ i32, i8, i8, i8, i8, void (%struct.single_obj*)*, i32 (%struct.single_obj*)* }>
	%struct.single_obj = type <{ %struct.single_class*, i32, i8, i8, i8, i8 }>

define void @method_incx(%struct.single_obj* %this) nounwind {
	%this.addr = alloca %struct.single_obj*		; <%struct.single_obj**> [#uses=3]
	store %struct.single_obj* %this, %struct.single_obj** %this.addr
	%1 = load %struct.single_obj** %this.addr		; <%struct.single_obj*> [#uses=1]
	%2 = getelementptr %struct.single_obj* %1, i32 0, i32 1		; <i32*> [#uses=1]
	%3 = load i32* %2		; <i32> [#uses=1]
	%4 = add i32 %3, 1		; <i32> [#uses=1]
	%5 = load %struct.single_obj** %this.addr		; <%struct.single_obj*> [#uses=1]
	%6 = getelementptr %struct.single_obj* %5, i32 0, i32 1		; <i32*> [#uses=1]
	store i32 %4, i32* %6
	ret void
}

define i32 @method_getx(%struct.single_obj* %this) nounwind {
	%1 = alloca i32		; <i32*> [#uses=2]
	%this.addr = alloca %struct.single_obj*		; <%struct.single_obj**> [#uses=2]
	store %struct.single_obj* %this, %struct.single_obj** %this.addr
	%2 = load %struct.single_obj** %this.addr		; <%struct.single_obj*> [#uses=1]
	%3 = getelementptr %struct.single_obj* %2, i32 0, i32 1		; <i32*> [#uses=1]
	%4 = load i32* %3		; <i32> [#uses=1]
	store i32 %4, i32* %1
	%5 = load i32* %1		; <i32> [#uses=1]
	ret i32 %5
}

define i32 @ll_main(i32 %junk) nounwind {
	%1 = alloca i32		; <i32*> [#uses=2]
	%junk.addr = alloca i32		; <i32*> [#uses=1]
	%the_class = alloca %struct.single_class, align 8		; <%struct.single_class*> [#uses=3]
	%the_obj = alloca %struct.single_obj*, align 8		; <%struct.single_obj**> [#uses=6]
	%vtable = alloca %struct.single_class*, align 8		; <%struct.single_class**> [#uses=3]
	%method = alloca void (%struct.single_obj*)*, align 8		; <void (%struct.single_obj*)**> [#uses=2]
	%meth2 = alloca i32 (%struct.single_obj*)*, align 8		; <i32 (%struct.single_obj*)**> [#uses=2]
	%result = alloca i32, align 4		; <i32*> [#uses=2]
	store i32 %junk, i32* %junk.addr
	%2 = getelementptr %struct.single_class* %the_class, i32 0, i32 5		; <void (%struct.single_obj*)**> [#uses=1]
	store void (%struct.single_obj*)* @method_incx, void (%struct.single_obj*)** %2
	%3 = getelementptr %struct.single_class* %the_class, i32 0, i32 6		; <i32 (%struct.single_obj*)**> [#uses=1]
	store i32 (%struct.single_obj*)* @method_getx, i32 (%struct.single_obj*)** %3
	%4 = call i8* @malloc(i64 16)		; <i8*> [#uses=1]
	%5 = bitcast i8* %4 to %struct.single_obj*		; <%struct.single_obj*> [#uses=1]
	store %struct.single_obj* %5, %struct.single_obj** %the_obj
	%6 = load %struct.single_obj** %the_obj		; <%struct.single_obj*> [#uses=1]
	%7 = getelementptr %struct.single_obj* %6, i32 0, i32 0		; <%struct.single_class**> [#uses=1]
	store %struct.single_class* %the_class, %struct.single_class** %7
	%8 = load %struct.single_obj** %the_obj		; <%struct.single_obj*> [#uses=1]
	%9 = getelementptr %struct.single_obj* %8, i32 0, i32 1		; <i32*> [#uses=1]
	store i32 0, i32* %9
	%10 = load %struct.single_obj** %the_obj		; <%struct.single_obj*> [#uses=1]
	%11 = getelementptr %struct.single_obj* %10, i32 0, i32 0		; <%struct.single_class**> [#uses=1]
	%12 = load %struct.single_class** %11		; <%struct.single_class*> [#uses=1]
	store %struct.single_class* %12, %struct.single_class** %vtable
	%13 = load %struct.single_class** %vtable		; <%struct.single_class*> [#uses=1]
	%14 = getelementptr %struct.single_class* %13, i32 0, i32 5		; <void (%struct.single_obj*)**> [#uses=1]
	%15 = load void (%struct.single_obj*)** %14		; <void (%struct.single_obj*)*> [#uses=1]
	store void (%struct.single_obj*)* %15, void (%struct.single_obj*)** %method
	%16 = load void (%struct.single_obj*)** %method		; <void (%struct.single_obj*)*> [#uses=1]
	%17 = load %struct.single_obj** %the_obj		; <%struct.single_obj*> [#uses=1]
	call void %16(%struct.single_obj* %17)
	%18 = load %struct.single_class** %vtable		; <%struct.single_class*> [#uses=1]
	%19 = getelementptr %struct.single_class* %18, i32 0, i32 6		; <i32 (%struct.single_obj*)**> [#uses=1]
	%20 = load i32 (%struct.single_obj*)** %19		; <i32 (%struct.single_obj*)*> [#uses=1]
	store i32 (%struct.single_obj*)* %20, i32 (%struct.single_obj*)** %meth2
	%21 = load i32 (%struct.single_obj*)** %meth2		; <i32 (%struct.single_obj*)*> [#uses=1]
	%22 = load %struct.single_obj** %the_obj		; <%struct.single_obj*> [#uses=1]
	%23 = call i32 %21(%struct.single_obj* %22)		; <i32> [#uses=1]
	store i32 %23, i32* %result
	%24 = load i32* %result		; <i32> [#uses=1]
	store i32 %24, i32* %1
	%25 = load i32* %1		; <i32> [#uses=1]
	ret i32 %25
}

declare i8* @malloc(i64)
