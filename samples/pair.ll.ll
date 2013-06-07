;
;
;  Classes, objects, and methods in llvm
;
; In a Java-ish pseudocode: 
;
; class Single {
;     int x;
;     void incx(int delta) { this.x += delta; }
;     int getval() { return this.x; }
; }
;
; class Pair extends Single {
;      int y;
;      void incy(int delta) { this.y += delta; }
;      int getval( ) { return this.x + this.y; }
; }
;

target triple = "x86_64-apple-macosx10.7.0"   
%Any = type i32  ; 'Any' class not defined yet

;;;; Code for class Single ;;;;




; Declare the layout of the class structure

%class_Single = type { 
    %Any*,                        ; Superclass pointer
    %obj_Single* ( )*,            ; constructor
    void ( %obj_Single*, i32)*,   ; void incx( this, int )
    i32 (%obj_Single*)*           ; int getval( this )
    }

; Declare layout of the object structure

%obj_Single = type { 
	%class_Single*, 				; class
	i32 							; x
	}

; The actual class object --- there is just one of these, however many 
; objects of this type there may be

@Single = global %class_Single {
    %Any* null, 						; Just a placeholder
    %obj_Single* ( )* @Single_constructor, 
    void (%obj_Single*, i32)* @Single_incx, 
    i32 (%obj_Single*)* @Single_getval
    }

; Methods in Single

define %obj_Single* @Single_constructor( ) {
   %memchunk = call i8* @malloc(i64 8)              ; 2 4-byte words of memory for object
   %as_obj = bitcast i8* %memchunk to %obj_Single*  ; cast it to a Single object
   ;; Allocation (above) should really be separate from initialization (below).  Allocation is 
   ;; done once, while initialization recursively calls superclass initializers
   %field_class_ptr = getelementptr inbounds %obj_Single* %as_obj, i32 0, i32 0   
   store %class_Single* @Single, %class_Single** %field_class_ptr
   %field_x = getelementptr inbounds %obj_Single* %as_obj, i32 0, i32 1
   store i32 0, i32* %field_x
   ret %obj_Single* %as_obj
   }
   

define void @Single_incx( %obj_Single* %this, i32 %delta) {
	%xaddr = getelementptr  %obj_Single* %this, i32 0, i32 1
	%xval = load i32* %xaddr
	%sum = add i32 %xval, %delta
	store i32 %sum, i32* %xaddr
	ret void
}

define i32 @Single_getval( %obj_Single* %this) {
	%xaddr = getelementptr  %obj_Single* %this, i32 0, i32 1
	%xval = load i32* %xaddr
	ret i32 %xval
}

;;;;;;;; Code for class Pair ;;;;;;;;;;
;
; class Pair extends Single {
;      int y;
;      void incy(int delta) { this.y += delta; }
;      int getval( ) { return this.x + this.y; }
; }

%class_Pair = type {
	%class_Single*,   			; class
    %obj_Pair* ( )*,            ; constructor
    void ( %obj_Pair*, i32)*,   ; void incx( this, int )   (inherited)
    i32 (%obj_Pair*)*,          ; int getval( this )       (overridden)
    void ( %obj_Pair*, i32)*   ; void incy( this, int )   (additional method)
    }
	
%obj_Pair = type { 
	%class_Pair*, 					; class
	i32, 							; x
	i32								; y
	}
	
define %obj_Pair* @Pair_constructor( ) {
   %memchunk = call i8* @malloc(i64 12)              ; 3 4-byte words of memory for object
   %as_obj = bitcast i8* %memchunk to %obj_Pair*     ; cast it to a Pair object
   ;; Allocation (above) should really be separate from initialization (below).  Allocation is 
   ;; done once, while initialization recursively calls superclass initializers
   %field_class_ptr = getelementptr inbounds %obj_Pair* %as_obj, i32 0, i32 0   
   store %class_Pair* @Pair, %class_Pair** %field_class_ptr
   %field_x = getelementptr inbounds %obj_Pair* %as_obj, i32 0, i32 1
   store i32 0, i32* %field_x
   %field_y = getelementptr inbounds %obj_Pair* %as_obj, i32 0, i32 2
   store i32 0, i32* %field_y
   ret %obj_Pair* %as_obj
   }

; Inherited method incx --- 
;    we need to tell llvm that it's ok to call it with a Pair instead of a Single
; 
@Pair_incx = alias void ( %obj_Pair*, i32)* bitcast (void ( %obj_Single*, i32)* @Single_incx to void ( %obj_Pair*, i32)*)

	
; New method incy
; 
define void @Pair_incy( %obj_Pair* %this, i32 %delta) {
	%yaddr = getelementptr  %obj_Pair* %this, i32 0, i32 2
	%yval = load i32* %yaddr
	%sum = add i32 %yval, %delta
	store i32 %sum, i32* %yaddr
	ret void
}

;  Overriding method getval  (must have signature compatible with Single_getval)
;
define i32 @Pair_getval( %obj_Pair* %this) {
	%xaddr = getelementptr  %obj_Pair* %this, i32 0, i32 1
	%xval = load i32* %xaddr
	%yaddr = getelementptr  %obj_Pair* %this, i32 0, i32 2
	%yval = load i32* %yaddr
	%sumval = add i32 %xval, %yval
	ret i32 %sumval
}

@Pair = global %class_Pair {
    %class_Single*  @Single, 					; superclass (for constructor, but we don't use it here)
    %obj_Pair* ( )* @Pair_constructor, 			; my own constructor
    void (%obj_Pair*, i32)* @Pair_incx, 		; inherited method incx
    i32 (%obj_Pair*)* @Pair_getval, 			; overridden method getval
    void (%obj_Pair*, i32)* @Pair_incy			; added method incy
    }


	
;  Woohoo.  Now let's create an object, call a couple methods, and return a value
define i32 @ll_main(i32 %delta) {
    ;;; A "Single" object
	%newish = getelementptr inbounds %class_Single* @Single, i32 0, i32 1
	%construct = load %obj_Single*( )** %newish
	%my_obj = call %obj_Single* %construct( )
	;; Call method incx with argument %delta
	%myobj_class = getelementptr inbounds %obj_Single* %my_obj, i32 0, i32 0
	%classptr = load %class_Single** %myobj_class
	%incx_method = getelementptr inbounds %class_Single* %classptr, i32 0, i32 2
	%call_me_maybe = load void (%obj_Single*, i32)** %incx_method
	call void %call_me_maybe(%obj_Single* %my_obj, i32 %delta)
	;; Do it again, just to see the effect
	call void %call_me_maybe(%obj_Single* %my_obj, i32 %delta)
	;; Now get the value with getval method
	%getval_method_addr = getelementptr inbounds %class_Single* %classptr, i32 0, i32 3
	%getval_method = load i32 (%obj_Single*)** %getval_method_addr
	%result = call i32 %getval_method(%obj_Single* %my_obj)
	;; 	ret i32 %result  --- suppress for now; check calls on Pair object below
	;
	;;; A "Pair" object
	%newPair_construct_field = getelementptr inbounds %class_Pair* @Pair, i32 0, i32 1
	%newPair_constructor = load %obj_Pair*( )** %newPair_construct_field
	%pair_obj = call %obj_Pair* %newPair_constructor( )

	;; Call method incx with argument %delta
	%pair_obj_class = getelementptr inbounds %obj_Pair* %pair_obj, i32 0, i32 0
	%pair_class_ptr = load %class_Pair** %pair_obj_class
	%pair_incx_method_addr = getelementptr inbounds %class_Pair* %pair_class_ptr, i32 0, i32 2
	%pair_incx_method = load void (%obj_Pair*, i32)** %pair_incx_method_addr
	call void %pair_incx_method(%obj_Pair* %pair_obj, i32 %delta)

	;; Call method incy with argument 13
	%pair_incy_method_addr = getelementptr inbounds %class_Pair* %pair_class_ptr, i32 0, i32 4
	%pair_incy_method = load void (%obj_Pair*, i32)** %pair_incy_method_addr
	call void %pair_incy_method(%obj_Pair* %pair_obj, i32 13)

	;; Get value of x+y to return 
	%getsum_method_addr = getelementptr inbounds %class_Pair* %pair_class_ptr, i32 0, i32 3
	%getsum_method = load i32 (%obj_Pair*)** %getsum_method_addr
	%sumval = call i32 %getsum_method(%obj_Pair* %pair_obj)
	;
	ret i32 %sumval

	
}
    
declare i8* @malloc(i64)



	


