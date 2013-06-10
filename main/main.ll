;#################################################################
;###                    auxiliary stuff                        ###
;#################################################################


target triple = "x86_64-apple-macosx10.7.0"
@str.format = private constant [3 x i8] c"%s\00"
@str.format2 = private constant [3 x i8] c"%d\00"
@emptychar = global i8 0
declare i8* @malloc(i64)
declare noalias i8* @GC_malloc(i64)
declare void @GC_init()
declare i32 @strcmp(i8*, i8*)

declare i32 @fprintf(%struct.__sFILE*, i8*, ...)
declare void @exit(i32) noreturn
@__stderrp = external global %struct.__sFILE*
%struct.__sFILE = type <{ i8*, i32, i32, i16, i16, i8, i8, i8, i8, %struct.__sbuf, i32, i8, i8, i8, i8, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i8, i8, i8, i8, i64 }>
%struct.__sFILEX = type opaque
%struct.__sbuf = type <{ i8*, i32, i8, i8, i8, i8 }>
@"\01LC" = internal constant [3 x i8] c"%s\00"
@"\01LC1" = internal constant [5 x i8] c"null\00"
@"\01LC2" = internal constant [38 x i8] c"Error: input cannot exceed 1000 chars\00"
declare i32 @printf(i8*, ...)
declare i32 @getchar()




;#################################################################
;###                       basic types                         ###
;#################################################################


;;;;;; Any class ;;;;;
%class_Any = type {
  %class_Any*,                               ; null parent pointer
  i1 ( %obj_Any*, %obj_Any* )*               ; Booln equals(this,x)
}


%obj_Any = type {
  %class_Any*                                ; class ptr
}


@Any = global %class_Any {
  %class_Any* null,                          ; null superclass ptr
  i1 (%obj_Any*, %obj_Any*)* @Any_equals     ; equals
}




define i1 @Any_equals(%obj_Any* %this, %obj_Any* %x) {
  %any1 = alloca i1                  ; <i1*> 
  %this.addr = alloca %obj_Any*		; <%struct.obj_Any**> 
  %x.addr = alloca %obj_Any*		    ; <%struct.obj_Any**> 
  %ret = alloca i1, align 4		    ; <i1*> 
  store %obj_Any* %this, %struct.obj_Any** %this.addr
  store %obj_Any* %x, %struct.obj_Any** %x.addr
  %any2 = load %obj_Any** %this.addr	; <%struct.obj_Any*> 
  %any3 = load %obj_Any** %x.addr		; <%struct.obj_Any*> 
  %any4 = icmp eq %obj_Any* %any2, %any3		; <i1> 
  br i1 %any4, label %any5, label %any6

  ; <label>
:any5	        	                ; preds = %0
  store i1 1, i1* %ret
  br label %any7

  ; <label>
:any6		                       ; preds = %0
  store i1 0, i1* %ret
  br label %any7

  ; <label>
:any7		                        ; preds = %0
  %any8 = load i1* %ret		        ; <i1> 
  store i1 %any8, i1* %any1
  %any9 = load i1* %any1		        ; <i1> 
  ret i1 %any9
}
;;;;;; IO class ;;;;;
%class_IO = type {
  %class_Any*,                               ; parent pointer
  i1 ( %obj_IO*, %obj_Any* )*,               ; Booln equals(this,x)
  %void ( %obj_IO*, i8* )*,     ; void abort(this, message)
  %class_IO* ( %obj_IO*, i8* )*,    ; IO out(this, message)
  i1 ( %obj_IO*, %obj_Any* )*,          ; Boolean is_null(this, arg)
  i8* ( %obj_IO* )*,              ; String in(this)
}


%obj_IO = type {
  %class_IO*,                                ; class ptr
}


@IO = global %class_IO {
  %class_Any* @Any,                          ; superclass
  i1 ( %obj_IO*, %obj_Any* )* @IO_equals,    ; equals
  void ( %obj_IO*, i8* )* @IO_abort,    ; abort
  %obj_IO* ( %obj_IO*, i8* )* @IO_out,    ; out
  i1 ( %obj_IO*, %obj_Any* )* @IO_is_null,  ; is_null
  i8* ( %obj_IO* )* @IO_in,              ; in
}


@IO_equals = alias i1 ( %obj_IO*, %obj_Any)* bitcast (i1 (%obj_Any*, %obj_Any*)* @Any_equals to i1 ( %obj_IO*, %obj_Any)*)
define void @IO_abort(%obj_IO %this, i8* message) {
  %this.addr = alloca %obj_IO*     ; <%struct.obj_IO**> 
  %message.addr = alloca i8*      ; i8** 
  store %obj_IO* %this, %struct.obj_IO** %this.addr
  store i8* %message, %i8** %message.addr
  %1 = load %__sFILE** @__stderrp      ; <%struct.__sFILE*> 
  %2 = load i8** %message.addr        ; i8* 
  %5 = call i32 (%__sFILE*, i8*, ...)* @fprintf(%struct.__sFILE* %1, i8* getelementptr ([3 x i8]* @"\01LC", i32 0, i32 0), i8* %2)
  call void @exit(i32 1) noreturn
  unreachable ; No predecessors!
  ret void
}
define %obj_IO* @IO_out(%obj_IO* %this, i8* %message) nounwind {
     %1 = alloca %obj_IO*     ; <%struct.obj_IO**> [#uses=2]
     %this.addr = alloca %obj_IO*     ; <%struct.obj_IO**> [#uses=2]
     %message.addr = alloca i8*      ; i8** [#uses=2]
     store %obj_IO* %this, %struct.obj_IO** %this.addr
     store i8* %message, i8** %message.addr
     %2 = load i8** %message.addr        ; i8*
     %5 = call i32 (i8*, ...)* @printf(i8* getelementptr ([3 x i8]* @"\01LC", i32 0, i32 0), i8* %2)     ; <i32>
     %6 = load %obj_IO** %this.addr       ; <%struct.obj_IO*>
     store %obj_IO* %6, %struct.obj_IO** %1
     %7 = load %obj_IO** %1       ; <%struct.obj_IO*>
     ret %obj_IO* %7
 } 
 define i1 @IO_is_null(%obj_IO* %this, %obj_Any* %arg) nounwind {
     %1 = alloca i1     ; <i1*> 
     %this.addr = alloca %obj_IO*     ; <%struct.obj_IO**> 
     %arg.addr = alloca %obj_Any*     ; <%struct.obj_Any**> 
     %ret = alloca i1, align 4      ; <i1*> 
     store %obj_IO* %this, %obj_IO** %this.addr
     store %obj_Any* %arg, %obj_Any** %arg.addr
     %2 = load %obj_Any** %arg.addr       ; <%struct.obj_Any*>
     %3 = icmp eq %obj_Any* %2, null      ; <i1> 
     br i1 %3, label %4, label %5
 
 ; <label>
 :4     ; preds = %0
     store i1 1, i1* %ret
     br label %6
 
 ; <label>
 :5     ; preds = %0
     store i1 0, i1* %ret
     br label %6
 
 ; <label>
 :6     ; preds = %5, %4
     %7 = load i1* %ret     ; <i1> 
     store i1 %7, i1* %1
     %8 = load i1* %1       ; <i1> 
     ret i1 %8
 }
define i8* @IO_in(%obj_IO* %this) nounwind {
 	%1 = alloca i8*		; <i8**> 
 	%this.addr = alloca %obj_IO*		; <%obj_IO**> 
 	%numchar = alloca i32, align 4		; <i32*> 
 	%c = alloca i8, align 1		; <i8*> 
 	%in = alloca i8*, align 8		; <i8**> 
 	%tmp = alloca [1000 x i8], align 1		; <[1000 x i8]*> 
 	store %obj_IO* %this, %obj_IO** %this.addr
 	store i32 0, i32* %numchar
 	br label %2
 
; <label>
:2		; preds = %21, %0
 	%3 = call i32 @getchar()		; <i32> 
 	%4 = trunc i32 %3 to i8		; <i8> 
 	store i8 %4, i8* %c
 	%5 = sext i8 %4 to i32		; <i32> 
 	%6 = icmp ne i32 %5, 10		; <i1> 
 	br i1 %6, label %7, label %22
 
; <label>
:7		; preds = %2
 	%8 = load i8* %c		; <i8> 
 	%9 = load i32* %numchar		; <i32> 
 	%10 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> 
 	%11 = sext i32 %9 to i64		; <i64> 
 	%12 = getelementptr i8* %10, i64 %11		; <i8*> 
 	store i8 %8, i8* %12
 	%13 = load i32* %numchar		; <i32> 
 	%14 = add i32 %13, 1		; <i32> 
 	store i32 %14, i32* %numchar
 	%15 = load i32* %numchar		; <i32> 
 	%16 = icmp sge i32 %15, 1000		; <i1> 
 	br i1 %16, label %17, label %21
 
; <label>
:17		; preds = %7
 	%18 = load %__sFILE** @__stderrp		; <%__sFILE*> 
 	%19 = call i32 (%__sFILE*, i8*, ...)* @fprintf(%__sFILE* %18, i8* getelementptr ([38 x i8]* @"\01LC2", i32 0, i32 0))		; <i32> 
 	%20 = call i32 (...)* @exit(i32 1)		; <i32> 
 	br label %21
 
; <label>
:21		; preds = %17, %7
 	br label %2
 
; <label>
:22		; preds = %2
 	%23 = load i32* %numchar		; <i32> 
 	%24 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> 
 	%25 = sext i32 %23 to i64		; <i64> 
 	%26 = getelementptr i8* %24, i64 %25		; <i8*> 
 	store i8 0, i8* %26
 	%27 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> 
 	store i8* %27, i8** %in
 	%28 = load i8** %in		; <i8*> 
 	store i8* %28, i8** %1
 	%29 = load i8** %1		; <i8*> 
 	ret i8* %29
}
;#################################################################
;###                     program types                         ###
;#################################################################


%class_Main = type { %class_IO*, 
%obj_%class_Main* ( )* ,        ; _Constructor 
 }
%obj_Main = type { %class_Main* }
@Main = global %class_Main {%class_IO* @IO ,
%obj_%class_Main* ( )* @%class_Main_constructor ,
 }

%class_Sieve = type { %class_IO*, 
%obj_%class_Sieve* ( i32 )* ,        ; _Constructor 
, %obj_Unit* (%obj_Sieve *, %obj_Int* ) * }
%obj_Sieve = type { %class_Sieve*, %obj_Int*, %obj_Sieve* }
@Sieve = global %class_Sieve {%class_IO* @IO ,
%obj_%class_Sieve* ( i32 )* @%class_Sieve_constructor ,
, %obj_Unit* (%obj_Sieve *, %obj_Int* ) * @__method_Sieve_test }



define %obj_Unit * @__method_Sieve_test(%obj_Sieve * %this, i32 %n) {
	; START If statement
	; START integer equality comparison
	; START Arithmetic operation (MULTEXPR)
	; START ID load (prime)
	%i0 = getelementptr %obj_Sieve* %this, i32 0, i32 1
	%i1 = load %obj_Int** %i0
	; END ID load (prime)
	; START Arithmetic operation (DIVEXPR)
	; START ID load (n)
	; START ID load (prime)
	%i2 = getelementptr %obj_Sieve* %this, i32 0, i32 1
	%i3 = load %obj_Int** %i2
	; END ID load (prime)
	; START instantiating Int
	%i4 = alloca %obj_Int*
	%i5 = getelementptr %obj_Int* null, i32 1
	%i6 = ptrtoint %obj_Int* %i5 to i64
	%i7 = call noalias i8* @GC_malloc(i64 %i6)
	%i8 = bitcast i8 * %i7 to %obj_Int*
	store %obj_Int* %i8, %obj_Int** %i4
	%i9 = load %obj_Int** %i4
	; setting class pointer
	%i10 = getelementptr %obj_Int* %i9, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i10
	; Setting new Int to default (0)
	%i11 = getelementptr %obj_Int* %i9, i32 0, i32 1
	store i32 0, i32 * %i11
	; END instantiating Int
	%i12 = load %obj_Int** %i4
	; Getting first parameter to binop
	%i13 = getelementptr i32 n, i32 0, i32 1
	; Getting second parameter to binop
	%i14 = getelementptr %obj_Int* %i3, i32 0, i32 1
	%i15 = load i32 * %i13
	%i16 = load i32 * %i14
	%i17 = sdiv i32  %i15, %i16
	%i18 = getelementptr %obj_Int* %i12, i32 0, i32 1
	store i32 %i17, i32 * %i18
	; END Arithmetic operation (DIVEXPR)
	; START instantiating Int
	%i19 = alloca %obj_Int*
	%i20 = getelementptr %obj_Int* null, i32 1
	%i21 = ptrtoint %obj_Int* %i20 to i64
	%i22 = call noalias i8* @GC_malloc(i64 %i21)
	%i23 = bitcast i8 * %i22 to %obj_Int*
	store %obj_Int* %i23, %obj_Int** %i19
	%i24 = load %obj_Int** %i19
	; setting class pointer
	%i25 = getelementptr %obj_Int* %i24, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i25
	; Setting new Int to default (0)
	%i26 = getelementptr %obj_Int* %i24, i32 0, i32 1
	store i32 0, i32 * %i26
	; END instantiating Int
	%i27 = load %obj_Int** %i19
	; Getting first parameter to binop
	%i28 = getelementptr %obj_Int* %i1, i32 0, i32 1
	; Getting second parameter to binop
	%i29 = load %obj_Int** %i4
	%i30 = getelementptr %obj_Int* %i29, i32 0, i32 1
	%i31 = load i32 * %i28
	%i32 = load i32 * %i30
	%i33 = mul i32  %i31, %i32
	%i34 = getelementptr %obj_Int* %i27, i32 0, i32 1
	store i32 %i33, i32 * %i34
	; END Arithmetic operation (MULTEXPR)
	%i35 = load %obj_Int** %i19
	%i36 = getelementptr %obj_Int* %i35, i32 0, i32 1
	%i37 = load i32 * %i36
	; START ID load (n)
	%i38 = getelementptr i32 n, i32 0, i32 1
	%i39 = load i32 * %i38
	%i40 = icmp eq  i32  %i37, %i39
	; START instantiating Boolean
	%i41 = alloca %obj_Boolean*
	%i42 = getelementptr %obj_Boolean* null, i32 1
	%i43 = ptrtoint %obj_Boolean* %i42 to i64
	%i44 = call noalias i8* @GC_malloc(i64 %i43)
	%i45 = bitcast i8 * %i44 to %obj_Boolean*
	store %obj_Boolean* %i45, %obj_Boolean** %i41
	%i46 = load %obj_Boolean** %i41
	; setting class pointer
	%i47 = getelementptr %obj_Boolean* %i46, i32 0, i32 0
	store %class_Boolean* @Boolean, %class_Boolean** %i47
	; Setting new Bool to default (false)
	; Setting bool value to false
	%i48 = getelementptr %obj_Boolean* %i46, i32 0, i32 1
	store i1 0, i1 * %i48
	; END instantiating Boolean
	%i49 = load %obj_Boolean** %i41
	%i50 = getelementptr %obj_Boolean* %i49, i32 0, i32 1
	store i1 %i40, i1 * %i50
	; END integer equality comparison
	%i51 = load %obj_Boolean** %i41
	%i52 = getelementptr %obj_Boolean* %i51, i32 0, i32 1
	%i53 = load i1 * %i52
	br i1  %i53, label %Label0, label %Label1
Label0:
	%i54 = bitcast %obj_Any* null to %obj_Unit*
	br label %Label2
Label1:
	; START If statement
	; START Method call (is_null)
	; START ID load (next)
	%i55 = getelementptr %obj_Sieve* %this, i32 0, i32 2
	%i56 = load %obj_Sieve** %i55
	; END ID load (next)
	; Get pointer to class of object
	%i57 = bitcast %obj_Sieve* %this to %obj_Sieve*
	%i58 = getelementptr %obj_Sieve* %i57, i32 0, i32 0
	%i59 = load %class_Sieve** %i58
	; getting method is_null(arg:Any):Boolean of IO
	%i60 = getelementptr %class_Sieve* %i59, i32 0, i32 -1
	%i61 = load %obj_Boolean* (%obj_IO *, %obj_Any* ) ** %i60
	%i62 = bitcast %obj_Sieve* %this to %obj_IO*
	; calling method is_null(arg:Any):Boolean
	%i63 = call %obj_Boolean* %i61(%obj_IO* %i62)
	; END Method call (is_null)
	%i64 = getelementptr %obj_Boolean* %i63, i32 0, i32 1
	%i65 = load i1 * %i64
	br i1  %i65, label %Label3, label %Label4
Label3:
	; START Method call (out_any)
	; START ID load (n)
	; Get pointer to class of object
	%i66 = bitcast %obj_Sieve* %this to %obj_Sieve*
	%i67 = getelementptr %obj_Sieve* %i66, i32 0, i32 0
	%i68 = load %class_Sieve** %i67
	; getting method out_any(arg:Any):IO of IO
	%i69 = getelementptr %class_Sieve* %i68, i32 0, i32 -1
	%i70 = load %obj_IO* (%obj_IO *, %obj_Any* ) ** %i69
	%i71 = bitcast %obj_Sieve* %this to %obj_IO*
	; calling method out_any(arg:Any):IO
	%i72 = call %obj_IO* %i70(%obj_IO* %i71)
	; END Method call (out_any)
	; START Method call (out)
	; START String literal ()
	; START instantiating String
	%i73 = alloca %obj_String*
	%i74 = getelementptr %obj_String* null, i32 1
	%i75 = ptrtoint %obj_String* %i74 to i64
	%i76 = call noalias i8* @GC_malloc(i64 %i75)
	%i77 = bitcast i8 * %i76 to %obj_String*
	store %obj_String* %i77, %obj_String** %i73
	%i78 = load %obj_String** %i73
	; setting class pointer
	%i79 = getelementptr %obj_String* %i78, i32 0, i32 0
	store %class_String* @String, %class_String** %i79
	; Setting new String to default (empty)
	%i80 = getelementptr %obj_String* %i78, i32 0, i32 1
	store i32 1, i32 * %i80
	%i81 = getelementptr %obj_String* %i78, i32 0, i32 2
	%i83 = call noalias i8* @GC_malloc(i64 1)
	%i82 = bitcast i8 * %i83 to [1 x i8]*
	store [1 x i8] c"\00", [1 x i8]* %i82
	%i84 = bitcast [1 x i8]* %i82 to i8 *
	store i8 * %i84, i8 ** %i81
	; END instantiating String
	%i85 = load %obj_String** %i73
	%i86 = getelementptr %obj_String* %i85, i32 0, i32 1
	store i32 2, i32 * %i86
	%i87 = getelementptr %obj_String* %i85, i32 0, i32 2
	%i89 = call noalias i8* @GC_malloc(i64 2)
	%i88 = bitcast i8 * %i89 to [2 x i8]*
	store [2 x i8] c" \00", [2 x i8]* %i88
	%i90 = bitcast [2 x i8]* %i88 to i8 *
	store i8 * %i90, i8 ** %i87
	; END String literal ()
	%i91 = load %obj_String** %i73
	; Get pointer to class of object
	%i92 = bitcast %obj_Sieve* %this to %obj_Sieve*
	%i93 = getelementptr %obj_Sieve* %i92, i32 0, i32 0
	%i94 = load %class_Sieve** %i93
	; getting method out(arg:String):IO of IO
	%i95 = getelementptr %class_Sieve* %i94, i32 0, i32 -1
	%i96 = load %obj_IO* (%obj_IO *, %obj_String* ) ** %i95
	%i97 = bitcast %obj_Sieve* %this to %obj_IO*
	; calling method out(arg:String):IO
	%i98 = call %obj_IO* %i96(%obj_IO* %i97, %obj_String* %i91)
	; END Method call (out)
	; Start ASSIGN
	%i99 = getelementptr %obj_Sieve* %this, i32 0, i32 2
	; START instantiating Sieve
	%i100 = alloca %obj_Sieve*
	%i101 = getelementptr %obj_Sieve* null, i32 1
	%i102 = ptrtoint %obj_Sieve* %i101 to i64
	%i103 = call noalias i8* @GC_malloc(i64 %i102)
	%i104 = bitcast i8 * %i103 to %obj_Sieve*
	store %obj_Sieve* %i104, %obj_Sieve** %i100
	%i105 = load %obj_Sieve** %i100
	; setting class pointer
	%i106 = getelementptr %obj_Sieve* %i105, i32 0, i32 0
	store %class_Sieve* @Sieve, %class_Sieve** %i106
	; START attribute prime:Int of Sieve
	%i107 = getelementptr %obj_Sieve* %i105, i32 0, i32 1
	; START instantiating Int
	%i108 = alloca %obj_Int*
	%i109 = getelementptr %obj_Int* null, i32 1
	%i110 = ptrtoint %obj_Int* %i109 to i64
	%i111 = call noalias i8* @GC_malloc(i64 %i110)
	%i112 = bitcast i8 * %i111 to %obj_Int*
	store %obj_Int* %i112, %obj_Int** %i108
	%i113 = load %obj_Int** %i108
	; setting class pointer
	%i114 = getelementptr %obj_Int* %i113, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i114
	; Setting new Int to default (0)
	%i115 = getelementptr %obj_Int* %i113, i32 0, i32 1
	store i32 0, i32 * %i115
	; END instantiating Int
	%i116 = load %obj_Int** %i108
	store %obj_Int* %i116, %obj_Int** %i107
	; END attribute prime:Int of Sieve
	; START attribute next:Sieve of Sieve
	%i117 = getelementptr %obj_Sieve* %i105, i32 0, i32 2
	store %obj_Sieve* null, %obj_Sieve** %i117
	; END attribute next:Sieve of Sieve
	; Initialize next:Sieve to introduced value
	%i118 = getelementptr %obj_Sieve* %i105, i32 0, i32 2
	%i119 = load %obj_Sieve** %i100
	%i120 = bitcast %obj_Any* null to %obj_Sieve*
	store %obj_Sieve* %i120, %obj_Sieve** %i118
	; END instantiating Sieve
	%i121 = load %obj_Sieve** %i100
	store %obj_Sieve* %i121, %obj_Sieve** %i99
	; End ASSIGN
	%i122 = load %obj_Sieve** %i100
	%i123 = bitcast %obj_Sieve* %i122 to %obj_Unit*
	br label %Label5
Label4:
	; START Method call (test)
	; START ID load (next)
	%i124 = getelementptr %obj_Sieve* %this, i32 0, i32 2
	%i125 = load %obj_Sieve** %i124
	; END ID load (next)
	; START ID load (n)
	; Get pointer to class of object
	%i126 = bitcast %obj_Sieve* %i125 to %obj_Sieve*
	%i127 = getelementptr %obj_Sieve* %i126, i32 0, i32 0
	%i128 = load %class_Sieve** %i127
	; getting method test(n:Int):Unit of Sieve
	%i129 = getelementptr %class_Sieve* %i128, i32 0, i32 1
	%i130 = load %obj_Unit* (%obj_Sieve *, %obj_Int* ) ** %i129
	%i131 = bitcast %obj_Sieve* %i125 to %obj_Sieve*
	; calling method test(n:Int):Unit
	%i132 = call %obj_Unit* %i130(%obj_Sieve* %i131)
	; END Method call (test)
	br label %Label5
Label5:
	%i133 = phi %obj_Unit* [ %i123, %Label3 ], [ %i132, %Label4 ]
	; END If statement
	br label %Label2
Label2:
	%i134 = phi %obj_Unit* [ %i54, %Label0 ], [ %i133, %Label1 ]
	; END If statement
	ret %obj_Unit* %i134
}

define i32 @ll_main() {
	call void @GC_init()

	; START instantiating Main
	%i135 = alloca %obj_Main*
	%i136 = getelementptr %obj_Main* null, i32 1
	%i137 = ptrtoint %obj_Main* %i136 to i64
	%i138 = call noalias i8* @GC_malloc(i64 %i137)
	%i139 = bitcast i8 * %i138 to %obj_Main*
	store %obj_Main* %i139, %obj_Main** %i135
	%i140 = load %obj_Main** %i135
	; setting class pointer
	%i141 = getelementptr %obj_Main* %i140, i32 0, i32 0
	store %class_Main* @Main, %class_Main** %i141
	; END instantiating Main
	%i142 = load %obj_Main** %i135
	; START instantiating Sieve
	%i143 = alloca %obj_Sieve*
	%i144 = getelementptr %obj_Sieve* null, i32 1
	%i145 = ptrtoint %obj_Sieve* %i144 to i64
	%i146 = call noalias i8* @GC_malloc(i64 %i145)
	%i147 = bitcast i8 * %i146 to %obj_Sieve*
	store %obj_Sieve* %i147, %obj_Sieve** %i143
	%i148 = load %obj_Sieve** %i143
	; setting class pointer
	%i149 = getelementptr %obj_Sieve* %i148, i32 0, i32 0
	store %class_Sieve* @Sieve, %class_Sieve** %i149
	; START attribute prime:Int of Sieve
	%i150 = getelementptr %obj_Sieve* %i148, i32 0, i32 1
	; START instantiating Int
	%i151 = alloca %obj_Int*
	%i152 = getelementptr %obj_Int* null, i32 1
	%i153 = ptrtoint %obj_Int* %i152 to i64
	%i154 = call noalias i8* @GC_malloc(i64 %i153)
	%i155 = bitcast i8 * %i154 to %obj_Int*
	store %obj_Int* %i155, %obj_Int** %i151
	%i156 = load %obj_Int** %i151
	; setting class pointer
	%i157 = getelementptr %obj_Int* %i156, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i157
	; Setting new Int to default (0)
	%i158 = getelementptr %obj_Int* %i156, i32 0, i32 1
	store i32 0, i32 * %i158
	; END instantiating Int
	%i159 = load %obj_Int** %i151
	store %obj_Int* %i159, %obj_Int** %i150
	; END attribute prime:Int of Sieve
	; START attribute next:Sieve of Sieve
	%i160 = getelementptr %obj_Sieve* %i148, i32 0, i32 2
	store %obj_Sieve* null, %obj_Sieve** %i160
	; END attribute next:Sieve of Sieve
	; Initialize next:Sieve to introduced value
	%i161 = getelementptr %obj_Sieve* %i148, i32 0, i32 2
	%i162 = load %obj_Sieve** %i143
	%i163 = bitcast %obj_Any* null to %obj_Sieve*
	store %obj_Sieve* %i163, %obj_Sieve** %i161
	; END instantiating Sieve
	; START instantiating Sieve
	%i164 = alloca %obj_Sieve*
	%i165 = getelementptr %obj_Sieve* null, i32 1
	%i166 = ptrtoint %obj_Sieve* %i165 to i64
	%i167 = call noalias i8* @GC_malloc(i64 %i166)
	%i168 = bitcast i8 * %i167 to %obj_Sieve*
	store %obj_Sieve* %i168, %obj_Sieve** %i164
	%i169 = load %obj_Sieve** %i164
	; setting class pointer
	%i170 = getelementptr %obj_Sieve* %i169, i32 0, i32 0
	store %class_Sieve* @Sieve, %class_Sieve** %i170
	; START attribute prime:Int of Sieve
	%i171 = getelementptr %obj_Sieve* %i169, i32 0, i32 1
	; START instantiating Int
	%i172 = alloca %obj_Int*
	%i173 = getelementptr %obj_Int* null, i32 1
	%i174 = ptrtoint %obj_Int* %i173 to i64
	%i175 = call noalias i8* @GC_malloc(i64 %i174)
	%i176 = bitcast i8 * %i175 to %obj_Int*
	store %obj_Int* %i176, %obj_Int** %i172
	%i177 = load %obj_Int** %i172
	; setting class pointer
	%i178 = getelementptr %obj_Int* %i177, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i178
	; Setting new Int to default (0)
	%i179 = getelementptr %obj_Int* %i177, i32 0, i32 1
	store i32 0, i32 * %i179
	; END instantiating Int
	%i180 = load %obj_Int** %i172
	store %obj_Int* %i180, %obj_Int** %i171
	; END attribute prime:Int of Sieve
	; START attribute next:Sieve of Sieve
	%i181 = getelementptr %obj_Sieve* %i169, i32 0, i32 2
	store %obj_Sieve* null, %obj_Sieve** %i181
	; END attribute next:Sieve of Sieve
	; Initialize next:Sieve to introduced value
	%i182 = getelementptr %obj_Sieve* %i169, i32 0, i32 2
	%i183 = load %obj_Sieve** %i164
	%i184 = bitcast %obj_Any* null to %obj_Sieve*
	store %obj_Sieve* %i184, %obj_Sieve** %i182
	; END instantiating Sieve
	%i185 = load %obj_Sieve** %i164
	store %obj_Sieve* %i185, %obj_Sieve** %i143
	; START instantiating Sieve
	%i186 = alloca %obj_Sieve*
	%i187 = getelementptr %obj_Sieve* null, i32 1
	%i188 = ptrtoint %obj_Sieve* %i187 to i64
	%i189 = call noalias i8* @GC_malloc(i64 %i188)
	%i190 = bitcast i8 * %i189 to %obj_Sieve*
	store %obj_Sieve* %i190, %obj_Sieve** %i186
	%i191 = load %obj_Sieve** %i186
	; setting class pointer
	%i192 = getelementptr %obj_Sieve* %i191, i32 0, i32 0
	store %class_Sieve* @Sieve, %class_Sieve** %i192
	; START attribute prime:Int of Sieve
	%i193 = getelementptr %obj_Sieve* %i191, i32 0, i32 1
	; START instantiating Int
	%i194 = alloca %obj_Int*
	%i195 = getelementptr %obj_Int* null, i32 1
	%i196 = ptrtoint %obj_Int* %i195 to i64
	%i197 = call noalias i8* @GC_malloc(i64 %i196)
	%i198 = bitcast i8 * %i197 to %obj_Int*
	store %obj_Int* %i198, %obj_Int** %i194
	%i199 = load %obj_Int** %i194
	; setting class pointer
	%i200 = getelementptr %obj_Int* %i199, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i200
	; Setting new Int to default (0)
	%i201 = getelementptr %obj_Int* %i199, i32 0, i32 1
	store i32 0, i32 * %i201
	; END instantiating Int
	%i202 = load %obj_Int** %i194
	store %obj_Int* %i202, %obj_Int** %i193
	; END attribute prime:Int of Sieve
	; START attribute next:Sieve of Sieve
	%i203 = getelementptr %obj_Sieve* %i191, i32 0, i32 2
	store %obj_Sieve* null, %obj_Sieve** %i203
	; END attribute next:Sieve of Sieve
	; Initialize next:Sieve to introduced value
	%i204 = getelementptr %obj_Sieve* %i191, i32 0, i32 2
	%i205 = load %obj_Sieve** %i186
	%i206 = bitcast %obj_Any* null to %obj_Sieve*
	store %obj_Sieve* %i206, %obj_Sieve** %i204
	; END instantiating Sieve
	; START instantiating Int
	%i207 = alloca %obj_Int*
	%i208 = getelementptr %obj_Int* null, i32 1
	%i209 = ptrtoint %obj_Int* %i208 to i64
	%i210 = call noalias i8* @GC_malloc(i64 %i209)
	%i211 = bitcast i8 * %i210 to %obj_Int*
	store %obj_Int* %i211, %obj_Int** %i207
	%i212 = load %obj_Int** %i207
	; setting class pointer
	%i213 = getelementptr %obj_Int* %i212, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i213
	; Setting new Int to default (0)
	%i214 = getelementptr %obj_Int* %i212, i32 0, i32 1
	store i32 0, i32 * %i214
	; END instantiating Int
	; START Int literal (2)
	; START instantiating Int
	%i215 = alloca %obj_Int*
	%i216 = getelementptr %obj_Int* null, i32 1
	%i217 = ptrtoint %obj_Int* %i216 to i64
	%i218 = call noalias i8* @GC_malloc(i64 %i217)
	%i219 = bitcast i8 * %i218 to %obj_Int*
	store %obj_Int* %i219, %obj_Int** %i215
	%i220 = load %obj_Int** %i215
	; setting class pointer
	%i221 = getelementptr %obj_Int* %i220, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i221
	; Setting new Int to default (0)
	%i222 = getelementptr %obj_Int* %i220, i32 0, i32 1
	store i32 0, i32 * %i222
	; END instantiating Int
	%i223 = load %obj_Int** %i215
	%i224 = getelementptr %obj_Int* %i223, i32 0, i32 1
	store i32 2, i32 * %i224
	; END Int literal (2)
	%i225 = load %obj_Int** %i215
	store %obj_Int* %i225, %obj_Int** %i207
	; START Int literal (2)
	; START instantiating Int
	%i226 = alloca %obj_Int*
	%i227 = getelementptr %obj_Int* null, i32 1
	%i228 = ptrtoint %obj_Int* %i227 to i64
	%i229 = call noalias i8* @GC_malloc(i64 %i228)
	%i230 = bitcast i8 * %i229 to %obj_Int*
	store %obj_Int* %i230, %obj_Int** %i226
	%i231 = load %obj_Int** %i226
	; setting class pointer
	%i232 = getelementptr %obj_Int* %i231, i32 0, i32 0
	store %class_Int* @Int, %class_Int** %i232
	; Setting new Int to default (0)
	%i233 = getelementptr %obj_Int* %i231, i32 0, i32 1
	store i32 0, i32 * %i233
	; END instantiating Int
	%i234 = load %obj_Int** %i226
	%i235 = getelementptr %obj_Int* %i234, i32 0, i32 1
	store i32 2, i32 * %i235
	; END Int literal (2)
	; START Method call (out)
	; START String literal (2)
	; START instantiating String
	%i236 = alloca %obj_String*
	%i237 = getelementptr %obj_String* null, i32 1
	%i238 = ptrtoint %obj_String* %i237 to i64
	%i239 = call noalias i8* @GC_malloc(i64 %i238)
	%i240 = bitcast i8 * %i239 to %obj_String*
	store %obj_String* %i240, %obj_String** %i236
	%i241 = load %obj_String** %i236
	; setting class pointer
	%i242 = getelementptr %obj_String* %i241, i32 0, i32 0
	store %class_String* @String, %class_String** %i242
	; Setting new String to default (empty)
	%i243 = getelementptr %obj_String* %i241, i32 0, i32 1
	store i32 1, i32 * %i243
	%i244 = getelementptr %obj_String* %i241, i32 0, i32 2
	%i246 = call noalias i8* @GC_malloc(i64 1)
	%i245 = bitcast i8 * %i246 to [1 x i8]*
	store [1 x i8] c"\00", [1 x i8]* %i245
	%i247 = bitcast [1 x i8]* %i245 to i8 *
	store i8 * %i247, i8 ** %i244
	; END instantiating String
	%i248 = load %obj_String** %i236
	%i249 = getelementptr %obj_String* %i248, i32 0, i32 1
	store i32 3, i32 * %i249
	%i250 = getelementptr %obj_String* %i248, i32 0, i32 2
	%i252 = call noalias i8* @GC_malloc(i64 3)
	%i251 = bitcast i8 * %i252 to [3 x i8]*
	store [3 x i8] c"2 \00", [3 x i8]* %i251
	%i253 = bitcast [3 x i8]* %i251 to i8 *
	store i8 * %i253, i8 ** %i250
	; END String literal (2)
	%i254 = load %obj_String** %i236
	; Get pointer to class of object
	%i255 = bitcast obj_Main* main2 to %obj_Main*
	%i256 = getelementptr %obj_Main* %i255, i32 0, i32 0
	%i257 = load %class_Main** %i256
	; getting method out(arg:String):IO of IO
	%i258 = getelementptr %class_Main* %i257, i32 0, i32 -1
	%i259 = load %obj_IO* (%obj_IO *, %obj_String* ) ** %i258
	%i260 = bitcast obj_Main* main2 to %obj_IO*
	; calling method out(arg:String):IO
	%i261 = call %obj_IO* %i259(%obj_IO* %i260, %obj_String* %i254)
	; END Method call (out)
	; START While loop
	br label %Label7
Label6:
	; Start ASSIGN
	; START Method call (test)
	; START ID load (s)
	; START ID load (i)
	%i262 = load %obj_Int** %i207
	; Get pointer to class of object
	%i263 = bitcast %obj_Sieve** %i143 to %obj_Sieve*
	%i264 = getelementptr %obj_Sieve* %i263, i32 0, i32 0
	%i265 = load %class_Sieve** %i264
	; getting method test(n:Int):Unit of Sieve
	%i266 = getelementptr %class_Sieve* %i265, i32 0, i32 1
	%i267 = load %obj_Unit* (%obj_Sieve *, %obj_Int* ) ** %i266
	%i268 = bitcast %obj_Sieve** %i143 to %obj_Sieve*
	; calling method test(n:Int):Unit
	%i269 = call %obj_Unit* %i267(%obj_Sieve* %i268, %obj_Int* %i262)
	; END Method call (test)
	br label %Label7
Label7:
	; START Boolean literal
	; START instantiating Boolean
	%i270 = alloca %obj_Boolean*
	%i271 = getelementptr %obj_Boolean* null, i32 1
	%i272 = ptrtoint %obj_Boolean* %i271 to i64
	%i273 = call noalias i8* @GC_malloc(i64 %i272)
	%i274 = bitcast i8 * %i273 to %obj_Boolean*
	store %obj_Boolean* %i274, %obj_Boolean** %i270
	%i275 = load %obj_Boolean** %i270
	; setting class pointer
	%i276 = getelementptr %obj_Boolean* %i275, i32 0, i32 0
	store %class_Boolean* @Boolean, %class_Boolean** %i276
	; Setting new Bool to default (false)
	; Setting bool value to false
	%i277 = getelementptr %obj_Boolean* %i275, i32 0, i32 1
	store i1 0, i1 * %i277
	; END instantiating Boolean
	%i278 = load %obj_Boolean** %i270
	; Setting bool value to true
	%i279 = getelementptr %obj_Boolean* %i278, i32 0, i32 1
	store i1 1, i1 * %i279
	; END Boolean literal
	%i280 = load %obj_Boolean** %i270
	%i281 = getelementptr %obj_Boolean* %i280, i32 0, i32 1
	%i282 = load i1 * %i281
	br i1  %i282, label %Label6, label %Label8
Label8:
	%i283 = alloca %obj_Any*
	store %obj_Any* null, %obj_Any** %i283
	%i284 = load %obj_Any** %i283
	; END While loop
	ret i32 0
}


