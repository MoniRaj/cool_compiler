/**
 *  CodeGenerator.java
 *
 *  Contains code generation module for Cool programming language,
 *  including class and object structures, class objects,
 *  method descriptors, and the main program.
 *
 *  Credit to original author: Nick CHaimov (nchaimov@uoregon.edu)
 *  @date: Winter 2010
 *
 *  Modified by: Paul Elliott and Monisha Balireddi (Spr 2013)
 *
 */
package main;
import ast.*;
import typecheck.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class CodeGenerator {
    
    protected HashMap<Integer, String> expr_types;
    
    static public final int ASSIGNEXPR = 1;
    static public final int IFEXPR = 2;
    static public final int DIVEXPR = 3;
    static public final int DOTEXPR = 4;
    static public final int EQUALSEXPR = 5;
    static public final int ERREXPR = 6;
    static public final int BOGUSEXPR = 7;
    static public final int LEEXPR = 8;
    static public final int LTEXPR = 9;
    static public final int MATCHEXPR = 10;
    static public final int MINUSEXPR = 11;
    static public final int MULTEXPR = 12;
    static public final int NEGEXPR = 13;
    static public final int NOTEXPR = 14;
    static public final int NUMEXPR = 15;
    static public final int PLUSEXPR = 16;
    static public final int PRIMARYEXPR = 17;
    static public final int WHILEEXPR = 18;

    private void log(final String msg) {
		if (debug) {
			System.err.println(msg);
		}
	}

    private void o(final String msg) {
        try {
            output.append(msg + "\n");
        }
        catch (Exception e) {
            System.err.println("Error occurred when appending code to buffer..likely problem: output buffer has not been initialized yet!");
            e.printStackTrace();
        }
    }

	public static class CodeGenerationException extends Exception {
		private static final long serialVersionUID = 4478662362211669244L;
		
		public CodeGenerationException(final String msg) {
			super(msg);
		}
	}
	
	public static class Register {
		public String name;
		public String type;
		
		public Register(final String name, final String type) {
			this.name = name;
			this.type = type;
		}
		
		public String pointerType() {
			return type + "*";
		}
		
		public String derefType() throws CodeGenerationException {
			if (type.endsWith("*")) {
				return type.substring(0, type.length() - 1);
			} else {
				throw new CodeGenerationException(
						"Can't dereference a non-pointer.");
			}
		}
		
		public String typeAndName() {
			return type + " " + name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	    
    protected final Environment.CoolClass NULL;
    protected final Environment.CoolClass NOTHING;
    protected final Environment.CoolClass ANY;
    protected final Environment.CoolClass UNIT;
    protected final Environment.CoolClass ARRAYANY;
    protected final Environment.CoolClass SYMBOL;
    protected final Environment.CoolClass BOOLEAN;
    protected final Environment.CoolClass INT;
    protected final Environment.CoolClass STRING;
    protected final Environment.CoolClass IO;
 
	protected int id;
	protected int label;
	
	protected final Environment env;
	
	protected final boolean debug;
	
	protected StringBuilder output;
	
	public CodeGenerator(final Environment env)
			throws Environment.EnvironmentException {
		this(env, false);
	}
	
	public CodeGenerator(final Environment env, final boolean debug)
			throws Environment.EnvironmentException {
		this.env = env;
		this.debug = debug;

        NOTHING = env.getClass("Nothing");
        NULL = env.getClass("Null");
        ANY = env.getClass("Any");
        ARRAYANY = env.getClass("ArrayAny");
        BOOLEAN = env.getClass("Boolean");
        UNIT = env.getClass("Unit");
        SYMBOL = env.getClass("Symbol");
        INT = env.getClass("Int");
        STRING = env.getClass("String");
        IO = env.getClass("IO");

        expr_types = new HashMap<Integer, String>();
        expr_types.put(1, "ASSIGNEXPR");
        expr_types.put(2, "IFEXPR");
        expr_types.put(3, "DIVEXPR");
        expr_types.put(4, "DOTEXPR");
        expr_types.put(5, "EQUALSEXPR");
        expr_types.put(6, "ERREXPR");
        expr_types.put(7, "BOGUSEXPR");
        expr_types.put(8, "LEEXPR");
        expr_types.put(9, "LTEXPR");
        expr_types.put(10, "MATCHEXPR");
        expr_types.put(11, "MINUSEXPR");
        expr_types.put(12, "MULTEXPR");
        expr_types.put(13, "NEGEXPR");
        expr_types.put(14, "NOTEXPR");
        expr_types.put(15, "NUMEXPR");
        expr_types.put(16, "PLUSEXPR");
        expr_types.put(17, "PRIMARYEXPR");
        expr_types.put(18, "WHILEEXPR");
	}
	
	public String nextID() {
		return "%i" + id++;
	}
	
	public Register nextRegister(final String type) {
		return new Register(nextID(), type);
	}
	
	public String nextLabel() {
		return "Label" + label++;
	}
	
	public String generateCode() {
		output = new StringBuilder();
		id = 0;
		label = 0;
		try {	
            log("\n--> Generating preamble: basic classes and such...");
			generatePreamble();
			log("\n--> Generating class descriptors...");
			generateClassDescriptors();
			log("\n--> Generating functions...");
			generateFunctions();
			log("\n--> Generating main function...");
			writeMainFunction();
			
		} catch (final Exception ex) {
			System.err.println("*** Code generation failed!");
			ex.printStackTrace();
			return "";
		}
		
		return output.toString();
	}

    /* This generates the class descriptors, class objects,
     * and method descriptors for the basic.cool classes
     */
    protected void generatePreamble() {
        o(";#################################################################");
        o(";###                    auxiliary stuff                        ###");
        o(";#################################################################");
        o("\n");
        //target triple
        //o("target triple = \"x86_64-apple-macosx10.7.0\"");
        o("target triple = \"x86_64-apple-darwin10.0\"");
        o("@str.format = private constant [3 x i8] c\"%s\\00\"");
        o("@str.format2 = private constant [3 x i8] c\"%d\\00\"");
        o("@emptychar = global i8 0");
        o("declare i8* @malloc(i64)");
        o("declare noalias i8* @GC_malloc(i64)");
        o("declare void @GC_init()");
        o("declare i32 @strcmp(i8*, i8*)\n");
        o("declare i32 @fprintf(%__sFILE*, i8*, ...)");
        o("declare i32 @exit(i32) noreturn");
        o("@__stderrp = external global %__sFILE*");
        o("%__sFILE = type <{ i8*, i32, i32, i16, i16, i8, i8, i8, i8, %__sbuf, i32, i8, i8, i8, i8, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %__sbuf, %__sFILEX*, i32, [3 x i8], [1 x i8], %__sbuf, i32, i8, i8, i8, i8, i64 }>");
        o("%__sFILEX = type opaque");
        o("%__sbuf = type <{ i8*, i32, i8, i8, i8, i8 }>");
        o("@\"\\01LC\" = internal constant [3 x i8] c\"%s\\00\"");
        o("@\"\\01LC1\" = internal constant [5 x i8] c\"null\\00\"");
        o("@\"\\01LC2\" = internal constant [38 x i8] c\"Error: input cannot exceed 1000 chars\\00\"");  
        o("declare i32 @printf(i8*, ...)");
        o("declare i32 @getchar()");
        o("\n");
        o("\n");

        o(";#################################################################");
        o(";###                       basic types                         ###");
        o(";#################################################################");
        o("\n");

        //nothing, null, any, arrayany, boolean, unit, symbol, int, string, io
        
        //PE: Do nothing for Nothing and Null just yet..
        //    Null can be implemented by setting the alloca pointer to null '0'
        //    Nothing may not have to be implemented if we don't get to case stmts

        //Unit, Int, String, and Boolean are basic types
       
        //TODO add this?
        //o(";;;;;; Unit class ;;;;;");
        //o("@the_Unit = global %obj_Unit { %class_Unit @Unit }");
        //o("\n");

        o(";;;;;; Any class ;;;;;");
        o("%class_Any = type {");
        o("  %class_Any*,                               ; null parent pointer");
        //o("  %obj_Any* ()*,                             ; constructor");
                //PE don't need a constructor for Any?
        //o("  %class_String* ( %obj_Any* )*,             ; String toString(this)");
        o("  i1 ( %obj_Any*, %obj_Any* )*               ; Booln equals(this,x)");
        o("}");
        o("\n");

        o("%obj_Any = type {");
        o("  %class_Any*                                ; class ptr");
        //o("  [100 x i8]*                                ; name");
        o("}");
        o("\n");

        o("@Any = global %class_Any {");
        o("  %class_Any* null,                          ; null superclass ptr");
        //o("  %obj_Any* ()* @Any_constructor,            ; constructor");
                //PE don't need a constructor for Any?
        //o("  %obj_String* (%obj_Any*)* @Any_toString,   ; toString");
        o("  i1 (%obj_Any*, %obj_Any*)* @Any_equals     ; equals");
        o("}");
        o("\n");
        o("\n");
/*
        o("define i8* @Any_toString(%obj_Any*) {");
        o("   ...");
        o("}");
*/

        o("define i1 @Any_equals(%obj_Any* %this, %obj_Any* %x) {");
        o("  %any1 = alloca i1                  ; <i1*> ");
        o("  %this.addr = alloca %obj_Any*		; <%obj_Any**> ");
        o("  %x.addr = alloca %obj_Any*		    ; <%obj_Any**> ");
        o("  %ret = alloca i1, align 4		    ; <i1*> ");
        o("  store %obj_Any* %this, %obj_Any** %this.addr");
        o("  store %obj_Any* %x, %obj_Any** %x.addr");
        o("  %any2 = load %obj_Any** %this.addr	; <%obj_Any*> ");
        o("  %any3 = load %obj_Any** %x.addr		; <%obj_Any*> ");
        o("  %any4 = icmp eq %obj_Any* %any2, %any3		; <i1> ");
        o("  br i1 %any4, label %Label5, label %Label6\n");
        o("  ; <label>:5	        	                ; preds = %0");
        o("Label5:  ");
        o("  store i1 1, i1* %ret");
        o("  br label %Label7\n");
        o("  ; <label>:6		                       ; preds = %0");
        o("Label6:  ");
        o("  store i1 0, i1* %ret");
        o("  br label %Label7\n");
        o("  ; <label>:7		                        ; preds = %0");
        o("Label7:  ");
        o("  store i1 0, i1* %ret");
        o("  %any8 = load i1* %ret		        ; <i1> ");
        o("  store i1 %any8, i1* %any1");
        o("  %any9 = load i1* %any1		        ; <i1> ");
        o("  ret i1 %any9");
        o("}");

/*
        o(";;;;;; ArrayAny class ;;;;;");
        o("%class_ArrayAny = type {");
        o("  %class_Any*,                               ; parent pointer");
        o("  %obj_ArrayAny* (i32)*,                     ; constructor");
        o("  %obj_String* ( %obj_ArrayAny* )*,          ; String toString(this)");
        o("  i1 ( %obj_ArrayAny*, %obj_Any* )*,         ; Booln equals(this,x)");
        o("  i32 ( %obj_ArrayAny* )*,                   ; length(this)");
        o("  void ( %obj_ArrayAny*, i32 )*,             ; resize(this, int)");
        o("  %obj_Any* ( %obj_ArrayAny*, i32 )*,        ; get(this, int)");
     o("  %obj_Any* ( %obj_ArrayAny*, i32, %obj_Any* )*,  ; set(this, int, Any)");
        o("}");
        o("\n");

        o("%obj_ArrayAny = type {");
        o("  %class_ArrayAny*,                          ; class ptr");
        //o("  [100 x i8]*                                ; name");
        o("  %i32,                                      ; length");
        o("  %obj_Any*                                  ; array_field");
        o("}");
        o("\n");

        o("@ArrayAny = global %class_ArrayAny {");
        o("  %class_Any* @Any,                          ; superclass ptr");
        o("  %obj_ArrayAny* (i32)* @ArrayAny_constr,    ; constructor");
        o("  %obj_String* (%obj_Any*)* @ArrayAny_toString, ; toString");
        o("  i1 (%obj_ArrayAny*, %obj_Any*)* @ArrayAny_equals,    ; equals");
        o("  i32 (%obj_ArrayAny*)* @ArrayAny_length,    ; length");
        o("  void (%obj_ArrayAny*, i32)* @ArrayAny_resize,    ; resize");
        o("  %obj_Any* (%obj_ArrayAny*, i32)* @ArrayAny_get,    ; get");
        o("  %obj_Any* (%obj_ArrayAny*, i32, %obj_Any*)* @ArrayAny_set,   ; set");
        o("}");
        o("\n");
        o("\n");

        //arrayany constructor

        o("@ArrayAny_toString = alias %obj_String* ( %obj_ArrayAny*)* bitcast (%obj_String* (%obj_Any*)* @Any_toString to %obj_String* ( %obj_ArrayAny*)*)");
        o("@ArrayAny_equals = alias i1 ( %obj_ArrayAny*, %obj_Any)* bitcast (i1 (%obj_Any*, %obj_Any*)* @Any_equals to i1 ( %obj_ArrayAny*, %obj_Any)*)");
        o("define i32 @ArrayAny_length(%obj_ArrayAny* %this) {");
        o("  %addr = getelementptr %obj_ArrayAny* %this, i32 0, i32 1 ;ld ln.adr);
        o("  %len = load i32* %addr  ; load len value");
        o("  ret %len  ; return length");
        o("}");

        //resize
        
        o("define %obj_Any* @ArrayAny_get(%obj_ArrayAny* %this, i32 %index) {");
        o("  %elt = alloca %obj_Any*   ; the returned elt);
        o("%1 = alloca %obj_Any*		; <%obj_Any**> [#uses=2]
        o("%this.addr = alloca %obj_ArrayAny*		; <%obj_ArrayAny**> [#uses=2]
        o("%index.addr = alloca i32		; <i32*> [#uses=2]
        o("store %obj_ArrayAny* %this, %obj_ArrayAny** %this.addr
        o("store i32 %index, i32* %index.addr
        o("%2 = load i32* %index.addr		; <i32> [#uses=1]
        o("%3 = load %obj_ArrayAny** %this.addr		; <%obj_ArrayAny*> [#uses=1]
        o("%4 = getelementptr %obj_ArrayAny* %3, i32 0, i32 6		; <%obj_Any***> [#uses=1]
        o("%5 = load %obj_Any*** %4		; <%obj_Any**> [#uses=1]
        o("%6 = sext i32 %2 to i64		; <i64> [#uses=1]
        o("%7 = getelementptr %obj_Any** %5, i64 %6		; <%obj_Any**> [#uses=1]
        o("%8 = load %obj_Any** %7		; <%obj_Any*> [#uses=1]
        o("store %obj_Any* %8, %obj_Any** %1
        o("%9 = load %obj_Any** %1		; <%obj_Any*> [#uses=1]
        o("ret %obj_Any* %9

*/
        //Symbol would go here
/*
        o(";;;;;; String class ;;;;;");
        o("%class_String = type {");
        o("  %class_Any*,                               ; parent pointer");
        o("  %obj_String* ( %obj_String* )*,            ; String toString(this)");
        o("  i1 ( %obj_String*, %obj_Any* )*,           ; Booln equals(this,x)");
        o("  i32 ( %obj_String* )*,                     ; Int length(this)");
        o("  %obj_String* ( %obj_String*, %obj_String* )*,  ; String concat(this, arg)");
        o("  %obj_String* ( %obj_String*, %obj_Int*, %obj_Int* )*,  ; String substring (this, start, end)");
        o("  i32 ( %obj_String*, %obj_Int* )*,  ; String charAt(this, index)");
        o("  i32 ( %obj_String*, %obj_String* )*  ; String indexOf(this, sub)");

        o("}");
        o("\n");

        o("%obj_String = type {");
        o("  %class_String*,                            ; class ptr");
        //o("  [100 x i8]*                                ; name");
        o("  %i32,                                      ; length");
        o("  i8*                                        ; str_field");
        o("}");
        o("\n");

        o("@String = global %class_String {");
        o("  %class_Any* @Any,                          ; superclass ptr");
        o("  %obj_String* (i32)* @String_constr,        ; constructor");
        o("  %obj_String* (%obj_Any*)* @String_toString, ; toString");//owrite
        o("  i1 (%obj_String*, %obj_String*)* @String_equals,  ; equals");//owrite
        o("  i32 (%obj_String*)* @String_length,        ; length");
        o("  %obj_String* (%obj_String*, %obj_String*)* @String_concat, ; cncat");
        o("  %obj_String* ( %obj_String*, i32, i32 )* @String_substring ; subst");
        o("  i32 ( %obj_String*, i32 )* @String_charAt, ; charAt");
        o("  i32 ( %obj_String*, %obj_String* )* @String_indexOf ; indexOf");
        o("}");
        o("\n");
        o("\n");
*/
        o(";;;;;; IO class ;;;;;");
        o("%class_IO = type {");
        o("  %class_Any*,                               ; parent pointer");
        //o("  %class_String* ( %obj_IO* )*,              ; String toString(this)");
        o("  i1 ( %obj_IO*, %obj_Any* )*,               ; Booln equals(this,x)");
        o("  void ( %obj_IO*, i8* )*,     ; void abort(this, message)");
        o("  %obj_IO* ( %obj_IO*, i8* )*,    ; IO out(this, message)");
        o("  i1 ( %obj_IO*, %obj_Any* )*,          ; Boolean is_null(this, arg)");
        //o("  %class_IO* ( %obj_IO*, %obj_Any* )*,   ; IO out_any(this, message)");
        o("  i8* ( %obj_IO* )*              ; String in(this)");
        //o("  %class_Symbol* ( %obj_IO*, %obj_String* )*,  ; Symbol symbol(this, name)");
        //o("  %class_String* ( %obj_IO*, %obj_Symbol* )*  ; String symbol_name(this, sym)");
        o("}");
        o("\n");

        o("%obj_IO = type {");
        o("  %class_IO*                                ; class ptr");
        //o("  [100 x i8]*                                ; name");
        o("}");
        o("\n");

        o("@IO = global %class_IO {");
        o("  %class_Any* @Any,                          ; superclass");
        //o("  %class_String* ( %obj_IO* )* @IO_toString, ; toString");
        o("  i1 ( %obj_IO*, %obj_Any* )* @IO_equals,    ; equals");
        o("  void ( %obj_IO*, i8* )* @IO_abort,    ; abort");
        o("  %obj_IO* ( %obj_IO*, i8* )* @IO_out,    ; out");
        o("  i1 ( %obj_IO*, %obj_Any* )* @IO_is_null,  ; is_null");
        //o("  %obj_IO* ( %obj_IO*, %obj_Any* )* @IO_out_any,   ; out_any");
        o("  i8* ( %obj_IO* )* @IO_in              ; in");
        //o("  %class_Symbol* ( %obj_IO*, %obj_String* )* @IO_symbol,  ; symbol");
        //o("  %class_String* ( %obj_IO*, %obj_Symbol* )* @IO_symbol_name ; symbol_name");
        o("}");
        o("\n");

        //o("@IO_toString = alias %obj_String* ( %obj_ArrayAny*)* bitcast (%obj_String* (%obj_Any*)* @Any_toString to %obj_String* ( %obj_ArrayAny*)*)");
        o("@IO_equals = alias i1 ( %obj_IO*, %obj_Any*)* bitcast (i1 (%obj_Any*, %obj_Any*)* @Any_equals to i1 ( %obj_IO*, %obj_Any*)*)");

        o("\n");
        o("define void @IO_abort(%obj_IO* %this, i8* %message) {");
        o("  %this.addr = alloca %obj_IO*     ; <%obj_IO**> ");
        o("  %message.addr = alloca i8*      ; i8** ");
        o("  store %obj_IO* %this, %obj_IO** %this.addr");
        o("  store i8* %message, i8** %message.addr");
        o("  %1 = load %__sFILE** @__stderrp      ; <%__sFILE*> ");
        o("  %2 = load i8** %message.addr        ; i8* ");
        o("  %3 = call i32 (%__sFILE*, i8*, ...)* @fprintf(%__sFILE* %1, i8* getelementptr ([3 x i8]* @\"\\01LC\", i32 0, i32 0), i8* %2)");
        o("  call i32 @exit(i32 1) noreturn");
        o("  unreachable ; No predecessors!");
        o("  ret void");
        o("}");

        o("define %obj_IO* @IO_out(%obj_IO* %this, i8* %message) nounwind {");
        o("     %1 = alloca %obj_IO*     ; <%obj_IO**> [#uses=2]");
        o("     %this.addr = alloca %obj_IO*     ; <%obj_IO**> [#uses=2]");
        o("     %message.addr = alloca i8*      ; i8** [#uses=2]");
        o("     store %obj_IO* %this, %obj_IO** %this.addr");
        o("     store i8* %message, i8** %message.addr");
        o("     %2 = load i8** %message.addr        ; i8*");
        o("     %3 = call i32 (i8*, ...)* @printf(i8* getelementptr ([3 x i8]* @\"\\01LC\", i32 0, i32 0), i8* %2)     ; <i32>");
        o("     %4 = load %obj_IO** %this.addr       ; <%obj_IO*>");
        o("     store %obj_IO* %4, %obj_IO** %1");
        o("     %5 = load %obj_IO** %1       ; <%obj_IO*>");
        o("     ret %obj_IO* %5");
        o(" } ");

        o(" define i1 @IO_is_null(%obj_IO* %this, %obj_Any* %arg) nounwind {");
        o("     %1 = alloca i1     ; <i1*> ");
        o("     %this.addr = alloca %obj_IO*     ; <%obj_IO**> ");
        o("     %arg.addr = alloca %obj_Any*     ; <%obj_Any**> ");
        o("     %ret = alloca i1, align 4      ; <i1*> ");
        o("     store %obj_IO* %this, %obj_IO** %this.addr");
        o("     store %obj_Any* %arg, %obj_Any** %arg.addr");
        o("     %2 = load %obj_Any** %arg.addr       ; <%obj_Any*>");
        o("     %3 = icmp eq %obj_Any* %2, null      ; <i1> ");
        o("     br i1 %3, label %Label4, label %Label5");
        o(" ");
        o(" ; <label>");
        o("Label4:     ; preds = %0");
        o("     store i1 1, i1* %ret");
        o("     br label %Label6");
        o(" ");
        o(" ; <label>");
        o("Label5:     ; preds = %0");
        o("     store i1 0, i1* %ret");
        o("     br label %Label6");
        o(" ");
        o(" ; <label>");
        o("Label6:     ; preds = %5, %4");
        o("     %4 = load i1* %ret     ; <i1> ");
        o("     store i1 %4, i1* %1");
        o("     %5 = load i1* %1       ; <i1> ");
        o("     ret i1 %5");
        o(" }");

/*TODO finish this possibly?
          o(" define %obj_IO @IO_out_any(%obj_IO* %this, %obj_Any* %arg) {");
          o(" ...");
          o(" }");
*/
        o("define i8* @IO_in(%obj_IO* %this) nounwind {");
        o(" 	%1 = alloca i8*		; <i8**> ");
        o(" 	%this.addr = alloca %obj_IO*		; <%obj_IO**> ");
        o(" 	%numchar = alloca i32, align 4		; <i32*> ");
        o(" 	%2 = alloca i8, align 1		; <i8*> ");
        o(" 	%in = alloca i8*, align 8		; <i8**> ");
        o(" 	%tmp = alloca [1000 x i8], align 1		; <[1000 x i8]*> ");
        o(" 	store %obj_IO* %this, %obj_IO** %this.addr");
        o(" 	store i32 0, i32* %numchar");
        o(" 	br label %Label2");
        o(" ");
        o("; <label>");
        o("Label2:		; preds = %21, %0");
        o(" 	%3 = call i32 @getchar()		; <i32> ");
        o(" 	%4 = trunc i32 %3 to i8		; <i8> ");
        o(" 	store i8 %4, i8* %2");
        o(" 	%5 = sext i8 %4 to i32		; <i32> ");
        o(" 	%6 = icmp ne i32 %5, 10		; <i1> ");
        o(" 	br i1 %6, label %Label7, label %Label22");
        o(" ");
        o("; <label>");
        o("Label7:		; preds = %2");
        o(" 	%7 = load i8* %2		; <i8> ");
        o(" 	%8 = load i32* %numchar		; <i32> ");
        o(" 	%9 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> ");
        o(" 	%10 = sext i32 %8 to i64		; <i64> ");
        o(" 	%11 = getelementptr i8* %9, i64 %10		; <i8*> ");
        o(" 	store i8 %7, i8* %11");
        o(" 	%12 = load i32* %numchar		; <i32> ");
        o(" 	%13 = add i32 %12, 1		; <i32> ");
        o(" 	store i32 %13, i32* %numchar");
        o(" 	%14 = load i32* %numchar		; <i32> ");
        o(" 	%15 = icmp sge i32 %14, 1000		; <i1> ");
        o(" 	br i1 %15, label %Label17, label %Label21");
        o(" ");
        o("; <label>");
        o("Label17:		; preds = %7");
        o(" 	%16 = load %__sFILE** @__stderrp		; <%__sFILE*> ");
        o(" 	%17 = call i32 (%__sFILE*, i8*, ...)* @fprintf(%__sFILE* %16, i8* getelementptr ([38 x i8]* @\"\\01LC2\", i32 0, i32 0))		; <i32> ");
        o("    call i32 @exit(i32 1) noreturn");
        o("    unreachable ; No predecessors!");
        o(" 	br label %Label21");
        o(" ");
        o("; <label>");
        o("Label21:		; preds = %17, %7");
        o(" 	br label %Label2");
        o(" ");
        o("; <label>");
        o("Label22:		; preds = %2");
        o(" 	%20 = load i32* %numchar		; <i32> ");
        o(" 	%21 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> ");
        o(" 	%22 = sext i32 %20 to i64		; <i64> ");
        o(" 	%23 = getelementptr i8* %21, i64 %22		; <i8*> ");
        o(" 	store i8 0, i8* %23");
        o(" 	%24 = getelementptr [1000 x i8]* %tmp, i32 0, i32 0	 ; <i8*> ");
        o(" 	store i8* %24, i8** %in");
        o(" 	%25 = load i8** %in		; <i8*> ");
        o(" 	store i8* %25, i8** %1");
        o(" 	%26 = load i8** %1		; <i8*> ");
        o(" 	ret i8* %26");
        o("}");
    }

	protected void generateClassDescriptors() {
        o(";#################################################################");
        o(";###                     program types                         ###");
        o(";#################################################################");
        o("\n");

        //For each class
		for (final Environment.CoolClass c : env.class_map.values()) {
            if (c.builtin) {
                continue;
            }
			final StringBuilder b = new StringBuilder();
			b.append(c.getInternalClassName());
			b.append(" = type { ");
			b.append(c.parent.getInternalClassName());
			b.append("*");
            
            
			int index = 1;
            //add constructor stuff
            b.append(", ");
            b.append("\n");
            b.append(c.getInternalInstanceName());
            b.append("*");
            b.append(" (");
            //cast c.node into class declaration
            ClassDecl decl = (ClassDecl) c.node;

            ClassVarFormals cvf = (ClassVarFormals) decl.varformals;
            
            //iterate over cvf.formalvarlist 
            System.out.println("HI");
            //Iterator iterator = cvf.formalvarlist.iterator();
            for (int i = 0; i < cvf.formalvarlist.size(); i++)
            //while(iterator.hasNext())
            {
                //ClassFormal cd = (ClassFormal) iterator.next();
                ClassFormal cd = (ClassFormal) cvf.formalvarlist.get(i);
                System.out.println("DEBUG");
                System.out.println(c.name);
                System.out.println(cd.type);
                System.out.println("ENDDEBUG");
                if(cd.type.equals("Int"))
                {
                    //add i32
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" i32");
                    }
                    else
                    {
                        b.append(" i32,");
                    }
                }
                else if(cd.type.equals("Boolean"))
                {
                    //add i1
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" i1");
                    }
                    else
                    {
                        b.append(" i1,");
                    }
                }
                else
                {
                    //add %obj_cd.type *
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" %obj_");
                        b.append(cd.type);
                        b.append("*");
                    }
                    else
                    {
                        b.append(" %obj_");
                        b.append(cd.type);
                        b.append("*,");
                    }
                }
            }
            b.append(" )* ");
            b.append("       ; _Constructor ");
            b.append("\n");
         
            
            
            
            
			for (final Environment.CoolMethod m : c.methods.values()) {
				if (m.owner.builtin && m.builtin_implementation == null) {
					continue;
				}
				m.index = index++;
				b.append(", ");
                b.append(m.getInternalType());
			}
			b.append(" }\n");
			
			b.append(c.getInternalInstanceName());
			b.append(" = type { ");
			b.append(c.getInternalClassName());
			b.append("*");
			
			if (c == INT) {
				b.append(", i32");
			}
			else if (c == BOOLEAN) {
				b.append(", i1");
			}
			
			for (final Environment.CoolAttribute a : c.attr_list) {
				b.append(", ");
                System.out.println("DEBUG");
                System.out.println(c);
                System.out.println(a);
                System.out.println(a.type);
                System.out.println("ENDDEBUG");
                 if (a.type == INT) {
                    b.append(" i32 ");
                }
                else if (a.type == BOOLEAN) {
                    b.append(" i1 ");
                }
                else {
                    b.append(a.type.getInternalInstanceName());
                    b.append("*");
                }
			}
			b.append(" }\n");
			
			// @_Classname = global %__class_Classname { %__class_Parentclass
			// @Parentclass, <method pointers...> }
			b.append(c.getInternalDescriptorName());
			b.append(" = global ");
			b.append(c.getInternalClassName());
			b.append(" {");
			b.append(c.parent.getInternalClassName());
			b.append("* ");
			b.append(c.parent.getInternalDescriptorName());
            
            b.append(" ,");
            b.append("\n");
            b.append(c.getInternalInstanceName());
            b.append("*");
            b.append(" (");
            
            //iterate over cvf.formalvarlist 
            
            //Iterator iterator = cvf.formalvarlist.iterator();
            for (int i = 0; i < cvf.formalvarlist.size(); i++)
            //while(iterator.hasNext())
            {
                //ClassFormal cd = (ClassFormal) iterator.next();
                ClassFormal cd = (ClassFormal) cvf.formalvarlist.get(i);
                System.out.println("DEBUG");
                System.out.println(c.name);
                System.out.println(cd.type);
                System.out.println("ENDDEBUG");
                if(cd.type.equals("Int"))
                {
                    //add i32
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" i32");
                    }
                    else
                    {
                        b.append(" i32,");
                    }
                }
                else if(cd.type.equals("Boolean"))
                {
                    //add i1
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" i1");
                    }
                    else
                    {
                        b.append(" i1,");
                    }
                }
                else
                {
                    //add %obj_cd.type *
                    if(i == (cvf.formalvarlist.size()-1))
                    {
                        b.append(" %obj_");
                        b.append(cd.type);
                        b.append("*");
                    }
                    else
                    {
                        b.append(" %obj_");
                        b.append(cd.type);
                        b.append("*,");
                    }
                }
            }
            //TODO this stuff is broken!
            b.append(" )* ");
            b.append("@");
            b.append(c.name);
            b.append("_constructor ");
            //b.append("       ; _Constructor ");
            b.append("\n");
            
			for (final Environment.CoolMethod m : c.methods.values()) {
				if (m.owner.builtin && m.builtin_implementation == null) {
					continue;
				}
				b.append(", ");
				b.append(m.getInternalType());
				b.append(" ");
				b.append(m.getInternalName());
			}
			b.append(" }\n");
			
			o(b.toString());
		}
		o("\n");
	}
	
	protected void generateFunctions() throws CodeGenerationException,
			Environment.EnvironmentException {
		
		for (final Environment.CoolClass c : env.class_map.values()) {
			for (final Environment.CoolMethod m : c.methods.values()) {
				if (m.owner.builtin && m.builtin_implementation == null) {
					continue;
				}
                StringBuilder head = new StringBuilder();
				head.append("define ");
				head.append(m.type.getInternalInstanceName());
				head.append(" * ");
				head.append(m.getInternalName());
				head.append("(").append(m.owner.getInternalInstanceName())
						.append(" * %this");
				int index = 1;
                int num_param_regs = 0;
				for (final Environment.CoolAttribute a : m.arguments) {
                    String type;
					a.index = index++;
					head.append(", ");
                    if (a.type == INT) {
                        type = "i32";
                        head.append("i32 %");
                    }
                    else if (a.type == BOOLEAN) {
                        type = "i1";
                        head.append("i1 %");
                    }
                    else {
                        type = "a.type.getInternalInstanceName()" + "*";
					    head.append(a.type.getInternalInstanceName());
					    head.append(" * %");
                    }
                    head.append(a.name);
                    final Register paramReg = new Register(a.name, type);
                    env.registers.push(a.name, paramReg);
                    num_param_regs += 1;
				}
				head.append(") {");
                o(head.toString());
				if (m.builtin_implementation != null) {
					o(m.builtin_implementation);
				} else {
					final Register r = new Register("%this", m.owner
							.getInternalInstanceName()
							+ "*");
                    generateFunctionBody(c, r, m);
                    for (int i = 0; i < num_param_regs; i++) {
                        env.registers.pop();
                    }
				}
				o("}\n");
			}
		}
	}
	
	protected void generateFunctionBody(final Environment.CoolClass cls,
			final Register thiz, final Environment.CoolMethod m)
			throws CodeGenerationException, Environment.EnvironmentException {
		if (m.node != null) {
			log(MessageFormat.format("Generating function body for {0} of {1}",
					m, cls));
			Register body = generate(cls, thiz, m.expr);
			if (!body.type.equals(m.type.getInternalInstanceName() + "*")) {
				body = bitcast(body, m.type.getInternalInstanceName() + "*");
			}
            StringBuilder fb = new StringBuilder();
			fb.append("\tret ").append(body.typeAndName());
            o(fb.toString());
		}
	}
	
	protected void comment(final String comment) {
		o("\t; " + comment);
	}
	
	protected Register generate(final Environment.CoolClass cls, Register thiz,
			final Expr e) throws CodeGenerationException,
			Environment.EnvironmentException {
		thiz = makeSinglePtr(thiz);
		if (e != null) {
			switch (e.expr_type) {
			
			case PRIMARYEXPR: {
                //Literals
                if (((PrimaryExpr) e).primarytype.equals("boolean")) {
                    comment("START Boolean literal");
                    final Register b = instantiate(BOOLEAN);
				    final Register bP = load(b);
                    if (((PrimaryExpr) e).bool == true) {
                        setBool(bP, true);
                    }
                    else {
                        setBool(bP, false);
                    }
				    comment("END Boolean literal");
				    return b;
			    }				
                else if (((PrimaryExpr) e).primarytype.equals("integer")) {
                    comment(MessageFormat.format(
                                "START Int literal ({0})", 
                                ((PrimaryExpr) e).integer));
				    final Register i = instantiate(INT);
				    final Register iP = load(i);
				    setInt(iP, ((PrimaryExpr) e).integer);
				    comment(MessageFormat.format(
                                "END Int literal ({0})", 
                                ((PrimaryExpr) e).integer));
	    			return i;
                }
                else if (((PrimaryExpr) e).primarytype.equals("string")) {
                  String str = ((PrimaryExpr) e).string;
                  final String v = str.replaceAll("[^A-Za-z0-9]",
                  "");
                  comment(MessageFormat.format("START String literal ({0})", v));
                  final Register strReg = instantiate(STRING);
                  final Register strP = load(strReg);
                  setString(strP, str);
                  comment(MessageFormat.format("END String literal ({0})", v));
                  return strReg;
                }
                else if (((PrimaryExpr) e).primarytype.equals("id")) {
                    comment(MessageFormat.format("START ID load ({0})",
                                ((PrimaryExpr) e).id));
                    final Register local = env.registers.get(
                            ((PrimaryExpr) e).id);
                    if (local != null) {
                        return local;
                    }
                    Environment.CoolClass curClass = cls;
                    Environment.CoolAttribute a = null;
                    while (a == null && curClass != ANY) {
                        a = curClass.attributes.get(((PrimaryExpr) e).id);
                        curClass = curClass.parent;
                    }
                    final int index = cls.attr_list.indexOf(a) + 1;
                    log("Attribute " + a + " is at index " + index + " of class "
                            + a.owner);
                    final Register idPtr = getElementPtr(thiz, a.type
                            .getInternalInstanceName()
                            + "**", 0, index);
                    final Register idInst = load(idPtr);
                    comment(MessageFormat.format("END ID load ({0})",
                                ((PrimaryExpr) e).id));
                    return idInst;
                }
                else if (((PrimaryExpr) e).primarytype.equals("this")) {
                    return thiz;
                }
                else if (((PrimaryExpr) e).primarytype.equals("new")) {
                    final Register newObj = instantiate(env
                            .getClass(((PrimaryExpr) e).type));
                    return newObj;
                }
                else if (((PrimaryExpr) e).primarytype.equals("null")) {
                    return new Register("null", ANY.getInternalInstanceName()
						+ "*"); //TODO is this ok?
                }
                else if (((PrimaryExpr) e).primarytype.equals("empty")) {
                    return new Register("null", ANY.getInternalInstanceName()
						+ "*"); //TODO is this ok?
                }
                //Block
                else if (((PrimaryExpr) e).primarytype.equals("block")) {
                    Block block = ((PrimaryExpr) e).block;
                    //Empty block
                    if (block.blockitems.size() == 0) {
                        return new Register("null", ANY.getInternalInstanceName()
                            + "*"); //TODO is this ok?
                    }
                    else {
                        int num_registers = 0;
                        int num_locals = 0;
                        ArrayList<Register> registers = new ArrayList<Register>();
                        for (int i = 0; i < block.blockitems.size(); i++) {
                            BlockItem bi = (BlockItem) block.blockitems.get(i);

                            if (!bi.id.equals("")) {
                                num_locals++;
                                final String name = bi.id;
                                final Environment.CoolClass type = env
                                    .getClass(bi.type);
                                final Register letVar = instantiate(type);
                                if (bi.expr != null) {
                                    final Register letValuePtr = generate(
                                            cls, thiz, bi.expr);
                                    final Register letValue = load(letValuePtr);
                                    store(letValue, letVar);
                                }
                                log(MessageFormat.format("Pushing {0} for {1}", 
                                            letVar.typeAndName(), name));
                                env.registers.push(name, letVar);
                            }
                            registers.add(generate(cls, thiz, bi.expr));
                            num_registers++;
                        }
                        for (int i = 0; i < num_locals; ++i) {
                            env.registers.pop();
                        }
                        return registers.get(num_registers-1);
                    }
                }
                //( expr )
                else if (((PrimaryExpr) e).primarytype.equals("parenexpr")) {
                    return generate(cls, thiz, ((PrimaryExpr) e).expr);
                }
                //super.methodcall
                else if (((PrimaryExpr) e).primarytype.equals("supercall")) {
                    comment(MessageFormat.format("START Method call ({0})",
                            ((DotExpr) e).id));
                    Register id = thiz;
                    Environment.CoolClass parentClass = cls.parent;

                    final List<Register> mArgs = processMethodArgs(cls, thiz,
                            ((PrimaryExpr) e).actuals);
                    final List<Register> args = new LinkedList<Register>();
                    String method_id = ((PrimaryExpr) e).id;
                    log("Looking up method " + method_id + " in " + parentClass);
                    final Environment.CoolMethod method = env.lookupMethod(
                            parentClass, method_id);
                    log("Will call method " + method + " at index " + method.index
                            + " of " + method.owner);

                    final int i = 0;
                    for (final Register r : mArgs) {
                        final String desiredType = method.arguments.get(i).type
                            .getInternalInstanceName()
                            + "*";
                        final String actualType = r.type;
                        if (!desiredType.equals(actualType)) {
                            if (actualType.startsWith(desiredType)) {
                                final Register q = load(r);
                                args.add(q);
                            }
                        } else {
                            args.add(r);
                        }

                    }

                    comment("Get pointer to class of object");
                    final Register castId = bitcast(id,
                            parentClass.getInternalInstanceName()
                            + "*");
                    final Register idClassPtr = getElementPtr(castId, 
                            parentClass.getInternalClassName()
                            + "**", 0, 0);
                    // Register clsCast = bitcast(idClassPtr,
                    // method.parent.getInternalClassName() + "**");
                    final Register idClass = makeSinglePtr(idClassPtr);
                    comment("getting method " + method + " of " + method.owner);
                    final Register methodPtr = getElementPtr(idClass, method
                            .getInternalType()
                            + "*", 0, method.index);
                    final Register methodInst = load(methodPtr);

                    final Register cast = bitcast(id, 
                            method.owner.getInternalInstanceName()
                            + "*");
                    StringBuilder supercomment = new StringBuilder();
                    supercomment.append("\t; calling method ").append(method);
                    o(supercomment.toString());
                    final Register call = call(methodInst, cast, method.type
                            .getInternalInstanceName()
                            + "*", args);

                    comment(MessageFormat.format(
                                "END Method call ({0})", method_id));
                    return call;

                }
                //this.methodcall
                else if (((PrimaryExpr) e).primarytype.equals("call")) {
                    comment(MessageFormat.format("START Method call ({0})",
                            ((PrimaryExpr) e).id));
                    Register id = thiz;
                    Environment.CoolClass curClass = cls;

                    final List<Register> mArgs = processMethodArgs(cls, thiz,
                            ((PrimaryExpr) e).actuals);
                    final List<Register> args = new LinkedList<Register>();
                    String method_id = ((PrimaryExpr) e).id;
                    log("Looking up method " + method_id + " in " + curClass);
                    final Environment.CoolMethod method = env.lookupMethod(
                            curClass, method_id);
                    log("Will call method " + method + " at index " + method.index
                            + " of " + method.owner);

                    final int i = 0;
                    for (final Register r : mArgs) {
                        final String desiredType = method.arguments.get(i).type
                            .getInternalInstanceName()
                            + "*";
                        final String actualType = r.type;
                        if (!desiredType.equals(actualType)) {
                            if (actualType.startsWith(desiredType)) {
                                final Register q = load(r);
                                args.add(q);
                            }
                        } else {
                            args.add(r);
                        }

                    }

                    comment("Get pointer to class of object");
                    final Register castId = bitcast(id, curClass
                            .getInternalInstanceName()
                            + "*");
                    final Register idClassPtr = getElementPtr(castId, curClass
                            .getInternalClassName()
                            + "**", 0, 0);
                    // Register clsCast = bitcast(idClassPtr,
                    // method.parent.getInternalClassName() + "**");
                    final Register idClass = makeSinglePtr(idClassPtr);
                    comment("getting method " + method + " of " + method.owner);
                    final Register methodPtr = getElementPtr(idClass, method
                            .getInternalType()
                            + "*", 0, method.index);
                    final Register methodInst = load(methodPtr);

                    final Register cast = bitcast(id, method.owner
                            .getInternalInstanceName()
                            + "*");
                    
                    StringBuilder thiscomment = new StringBuilder();
                    thiscomment.append("\t; calling method ").append(method);
                    o(thiscomment.toString());
                    final Register call = call(methodInst, cast, method.type
                            .getInternalInstanceName()
                            + "*", args);

                    comment(MessageFormat.format(
                                "END Method call ({0})", method_id));
                    return call;

                }
            }

            //expr.methodcall
            case DOTEXPR: {
				comment(MessageFormat.format("START Method call ({0})",
                            ((DotExpr) e).id));
				Register id = thiz;
				Environment.CoolClass curClass = cls;
                //left.class_type is null! why???
                Expr left = ((DotExpr) e).expr;
				if (left != null) {
					id = generate(cls, thiz, left);
					curClass = left.class_type;
					log(MessageFormat.format(
							"Target of method invocation is {0} of type {1}",
							id.typeAndName(), cls));
				}
				
				final List<Register> mArgs = processMethodArgs(cls, thiz,
						((DotExpr) e).actuals);
				final List<Register> args = new LinkedList<Register>();
                
                String emethod_id = ((DotExpr) e).id;
				log("Looking up method " + emethod_id + " in " + curClass);
				final Environment.CoolMethod method = env.lookupMethod(
						curClass, emethod_id);
				log("Will call method " + method + " at index " + method.index
						+ " of " + method.owner);
				
				final int i = 0;
				for (final Register r : mArgs) {
					final String desiredType = method.arguments.get(i).type
							.getInternalInstanceName()
							+ "*";
					final String actualType = r.type;
					if (!desiredType.equals(actualType)) {
						if (actualType.startsWith(desiredType)) {
							final Register q = load(r);
							args.add(q);
						}
					} else {
						args.add(r);
					}
					
				}
				
				comment("Get pointer to class of object");
				final Register castId = bitcast(id, curClass
						.getInternalInstanceName()
						+ "*");
				final Register idClassPtr = getElementPtr(castId, curClass
						.getInternalClassName()
						+ "**", 0, 0);
				// Register clsCast = bitcast(idClassPtr,
				// method.parent.getInternalClassName() + "**");
				final Register idClass = makeSinglePtr(idClassPtr);
				comment("getting method " + method + " of " + method.owner);
				final Register methodPtr = getElementPtr(idClass, method
						.getInternalType()
						+ "*", 0, method.index);
				final Register methodInst = load(methodPtr);
				
				final Register cast = bitcast(id, method.owner
						.getInternalInstanceName()
						+ "*");
				
                StringBuilder ecomment = new StringBuilder();
                ecomment.append("\t; calling method ").append(method);
                o(ecomment.toString());
				final Register call = call(methodInst, cast, method.type
						.getInternalInstanceName()
						+ "*", args);
				
				comment(MessageFormat.format("END Method call ({0})",emethod_id));
				return call;
			}

			//Assignments	
			case ASSIGNEXPR: {
				comment("Start ASSIGN");
				
				// Get attribute location
				final String id = ((AssignExpr) e).id;
				final Register local = env.registers.get(id);
				if (local != null) {
					return local;
				}

                // Find attribute in the chain
				Environment.CoolClass curClass = cls;
				Environment.CoolAttribute a = null;
				while (a == null && curClass != ANY) {
					a = curClass.attributes.get(id);
					curClass = curClass.parent;
				}
				final int index = cls.attr_list.indexOf(a) + 1;
				log("Attribute " + a + " is at index " + index + " of class "
						+ a.owner);

                // Create register for var
				final Register thizInst = makeSinglePtr(thiz);
				final Register idPtr = getElementPtr(thizInst, a.type
						.getInternalInstanceName()
						+ "**", 0, index);
				
                // Create register for var value
				final Register rightSide = generate(cls, thiz, 
                        ((AssignExpr) e).expr);
				final Register rightInst = makeSinglePtr(rightSide);
				
                // Store value in var
				store(rightInst, idPtr);
				
				comment("End ASSIGN");
				return rightSide;
			}
				
			//If statement	
			case IFEXPR: {
				comment("START If statement");
                Expr ifcond = ((IfExpr) e).expr1;
                Expr ifexpr = ((IfExpr) e).expr2;
                Expr elseexpr = ((IfExpr) e).expr3;
				final Register cond = generate(cls, thiz, ifcond);
				final Register condLoad = makeSinglePtr(cond);
				final Register condPtr = getElementPtr(condLoad, "i1 *", 0, 1);
				final Register condVal = load(condPtr);
				final String trueBranch = nextLabel();
				final String falseBranch = nextLabel();
				final String doneBranch = nextLabel();
				branch(condVal, trueBranch, falseBranch);
				writeLabel(trueBranch);
				Register trueResult = generate(cls, thiz, ifexpr);
				trueResult = makeSinglePtr(trueResult);
				if (!trueResult.type.equals(e.class_type.getInternalInstanceName()
						+ "*")) {
					trueResult = bitcast(trueResult, e.class_type
							.getInternalInstanceName()
							+ "*");
				}
				branch(doneBranch);
				writeLabel(falseBranch);
				Register falseResult = generate(cls, thiz, elseexpr);
				falseResult = makeSinglePtr(falseResult);
				if (!falseResult.type.equals(e.class_type.getInternalInstanceName()
						+ "*")) {
					falseResult = bitcast(falseResult, e.class_type
							.getInternalInstanceName()
							+ "*");
				}
				branch(doneBranch);
				writeLabel(doneBranch);
				final Register ifResult = nextRegister(e.class_type
						.getInternalInstanceName()
						+ "*");
                StringBuilder ifstr = new StringBuilder();
				ifstr.append("\t").append(ifResult.name).append(" = phi ")
						.append(ifResult.type).append(" [ ").append(
								trueResult.name).append(", %").append(
								trueBranch).append(" ], [ ").append(
								falseResult.name).append(", %").append(
								falseBranch).append(" ]");
                o(ifstr.toString());
				comment("END If statement");
				return ifResult;
			}
			
            //TODO case statement?

            //While stmt
			case WHILEEXPR: {
				comment("START While loop");
				final String loopHead = nextLabel();
				final String loopTest = nextLabel();
				final String afterLoop = nextLabel();
				branch(loopTest);
				writeLabel(loopHead);
				@SuppressWarnings("unused")
                Expr whilecond = ((WhileExpr) e).expr1;
                Expr whileexpr = ((WhileExpr) e).expr2;
				final Register loop = generate(cls, thiz, whileexpr);
				branch(loopTest);
				writeLabel(loopTest);
				final Register cond = generate(cls, thiz, whilecond);
				final Register condLoad = load(cond);
				final Register condPtr = getElementPtr(condLoad, "i1 *", 0, 1);
				final Register condVal = load(condPtr);
				branch(condVal, loopHead, afterLoop);
				writeLabel(afterLoop);
				final Register resultPtr = nextRegister(ANY
						.getInternalInstanceName()
						+ "**");
				alloca(resultPtr);
				store(new Register("null", ANY.getInternalInstanceName()
						+ "*"), resultPtr);
				final Register result = load(resultPtr);
				comment("END While loop");
				return result;
			}
			
            //Not (!)
			case NOTEXPR: {
				comment("START not");
				final Register cond = generate(cls, thiz, ((NotExpr) e).expr);
				final Register condLoad = makeSinglePtr(cond);
				final Register condPtr = getElementPtr(condLoad, "i1 *", 0, 1);
				final Register condVal = load(condPtr);
				
				final Register resVal = nextRegister("i1");
                StringBuilder notStr = new StringBuilder();
				notStr.append("\t").append(resVal).append(" = icmp ne ")
						.append(condVal.typeAndName()).append(", 0");
                o(notStr.toString());
				
				final Register resultPtr = instantiate(BOOLEAN);
				final Register result = load(resultPtr);
				final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
				store(resVal, boolPtr);
				
				comment("END not");
				return resultPtr;
			}
				
            //LE (<=) and LT (<) ops
			case LEEXPR:
			case LTEXPR: {
				comment("START less-than comparison");
				final Register int1 = generate(cls, thiz, ((BinExpr) e).l);
				final Register int1load = makeSinglePtr(int1);
				final Register int1Ptr = getElementPtr(int1load, "i32 *", 0, 1);
				final Register int1Val = load(int1Ptr);
				
				final Register int2 = generate(cls, thiz, ((BinExpr) e).r);
				final Register int2load = makeSinglePtr(int2);
				final Register int2Ptr = getElementPtr(int2load, "i32 *", 0, 1);
				final Register int2Val = load(int2Ptr);
				
				String op;
				if (e.expr_type == LEEXPR) {
					op = "sle";
				} else {
					op = "slt";
				}
				
				final Register resVal = nextRegister("i1");
                StringBuilder cmpStr = new StringBuilder();
				cmpStr.append("\t").append(resVal).append(" = icmp ")
						.append(op).append(" ").append(int1Val.typeAndName())
						.append(", ").append(int2Val.name).append("\n");
				o(cmpStr.toString());

				final Register resultPtr = instantiate(BOOLEAN);
				final Register result = load(resultPtr);
				final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
				store(resVal, boolPtr);
				
				comment("END less-than comparison");
				return resultPtr;
			}
				
			//Math ops (+ - * /)
            case PLUSEXPR:
			case MINUSEXPR:
			case MULTEXPR:
			case DIVEXPR: {
				comment(MessageFormat.format(
						"START Arithmetic operation ({0})", 
                            expr_types.get(e.expr_type)));
				final Register arg1 = generate(cls, thiz, ((BinExpr) e).l);
				final Register arg2 = generate(cls, thiz, ((BinExpr) e).r);
				final Register result = intOpt(((BinExpr) e).expr_type,arg1,arg2);
				comment(MessageFormat.format("END Arithmetic operation ({0})",
                        expr_types.get(e.expr_type)));
				return result;
			}
				
			//Test equality (==)
            case EQUALSEXPR: {
                Expr left = ((BinExpr) e).l;
                Expr right = ((BinExpr) e).r;
				if (left.class_type == INT) {
					comment("START integer equality comparison");
					final Register int1 = generate(cls, thiz, left);
					final Register int1load = makeSinglePtr(int1);
					final Register int1Ptr = getElementPtr(int1load, "i32 *",
							0, 1);
					final Register int1Val = load(int1Ptr);
					
					final Register int2 = generate(cls, thiz, right);
					final Register int2load = makeSinglePtr(int2);
					final Register int2Ptr = getElementPtr(int2load, "i32 *",
							0, 1);
					final Register int2Val = load(int2Ptr);
					
					final Register resVal = nextRegister("i1");
                    StringBuilder sb = new StringBuilder();
					sb.append("\t").append(resVal).append(" = icmp eq ")
							.append(" ").append(int1Val.typeAndName()).append(
									", ").append(int2Val.name);
                    o(sb.toString());
					final Register resultPtr = instantiate(BOOLEAN);
					final Register result = load(resultPtr);
					final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
					store(resVal, boolPtr);
					
					comment("END integer equality comparison");
					return resultPtr;
				} else if (left.class_type == BOOLEAN) {
					comment("START bool equality comparison");
					final Register int1 = generate(cls, thiz, left);
					final Register int1load = makeSinglePtr(int1);
					final Register int1Ptr = getElementPtr(int1load, "i1 *", 0,
							1);
					final Register int1Val = load(int1Ptr);
					
					final Register int2 = generate(cls, thiz, right);
					final Register int2load = makeSinglePtr(int2);
					final Register int2Ptr = getElementPtr(int2load, "i1 *", 0,
							1);
					final Register int2Val = load(int2Ptr);
					
					final Register resVal = nextRegister("i1");
                    StringBuilder sb2 = new StringBuilder();
					sb2.append("\t").append(resVal).append(" = icmp eq ")
							.append(" ").append(int1Val.typeAndName()).append(
									", ").append(int2Val.name);
                    o(sb2.toString());
					final Register resultPtr = instantiate(BOOLEAN);
					final Register result = load(resultPtr);
					final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
					store(resVal, boolPtr);
					
					comment("END bool equality comparison");
					return resultPtr;
				} else if (left.class_type == STRING) {
					comment("START string equality comparison");
					final Register int1 = generate(cls, thiz, left);
					final Register int1load = makeSinglePtr(int1);
					final Register int1Ptr = getElementPtr(int1load, "i8 **",
							0, 2);
					final Register int1Val = load(int1Ptr);
					
					final Register int2 = generate(cls, thiz, right);
					final Register int2load = makeSinglePtr(int2);
					final Register int2Ptr = getElementPtr(int2load, "i8 **",
							0, 2);
					final Register int2Val = load(int2Ptr);
					
					final Register call = nextRegister("i32");
					
                    StringBuilder sb3 = new StringBuilder();
					sb3.append("\t").append(call.name).append(
							" = call i32 @strcmp(").append(
							int1Val.typeAndName()).append(", ").append(
							int2Val.typeAndName()).append(")");
                    o(sb3.toString());
					
					final Register resVal = nextRegister("i1");
                    StringBuilder sb4 = new StringBuilder();
					sb4.append("\t").append(resVal).append(" = icmp eq ")
							.append(" ").append(call.typeAndName()).append(
									", 0");
                    o(sb4.toString());
					
					final Register resultPtr = instantiate(BOOLEAN);
					final Register result = load(resultPtr);
					final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
					store(resVal, boolPtr);
					
					comment("END string equality comparison");
					return resultPtr;
				}
                else {
					comment("START object equality comparison");
					final Register arg1 = generate(cls, thiz, left);
					final Register arg2 = generate(cls, thiz, right);
					
					final Register resVal = nextRegister("i1");
                    StringBuilder sb5 = new StringBuilder();
					sb5.append("\t").append(resVal).append(" = icmp eq ")
							.append(" ").append(arg1.typeAndName())
							.append(", ").append(arg2.name);
                    o(sb5.toString());
					
					final Register resultPtr = instantiate(BOOLEAN);
					final Register result = load(resultPtr);
					final Register boolPtr = getElementPtr(result, "i1 *", 0, 1);
					store(resVal, boolPtr);
					comment("END object equality comparison");
					return resultPtr;
				}
			}
				
			case NEGEXPR: {
				comment("START negation");
				final Register arg1 = generate(cls, thiz,
                        ((NegExpr) e).expr);
				final Register zeroPtr = instantiate(INT);
				final Register zero = load(zeroPtr);
				setInt(zero, 0);
				final Register result = intOpt(MINUSEXPR, zero, arg1);
				comment("END negation");
				return result;
			}
				
			default:
				if (debug) {
					log("Catastrophe! Tried to generate a non-expr type");
				} else {
					throw new CodeGenerationException(
                            "Catastrophe! Tried to generate a non-expr type");
				}
			}
		}
		return null;
	}
		
	protected Register makeSinglePtr(final Register ptr)
			throws CodeGenerationException {
		Register result = ptr;
		while (result.type.endsWith("**")) {
			result = load(result);
		}
		return result;
	}
	
	protected void branch(final String label) {
        StringBuilder branch = new StringBuilder();
        branch.append("\tbr label %").append(label);
        o(branch.toString());
	}
	
	protected void branch(final Register cond, final String trueBranch,
			final String falseBranch) {
        StringBuilder branch = new StringBuilder();
        branch.append("\tbr ").append(cond.typeAndName()).append(", label %");
        branch.append(trueBranch).append(", label %").append(falseBranch);
        o(branch.toString());
	}
	
	protected void writeLabel(final String label) {
		o(label + ":");
	}
	
	private Register intOpt(final int kind, final Register r1, final Register r2)
			throws CodeGenerationException, Environment.EnvironmentException {
		final Register result = instantiate(INT);
		final Register resInst = load(result);
		
		comment("Getting first parameter to binop");
		final Register r1Inst = makeSinglePtr(r1);
		final Register r1IntPtr = getElementPtr(r1Inst, "i32 *", 0, 1);
		
		comment("Getting second parameter to binop");
		final Register r2Inst = makeSinglePtr(r2);
		final Register r2IntPtr = getElementPtr(r2Inst, "i32 *", 0, 1);
		
		final Register r1Int = load(r1IntPtr);
		final Register r2Int = load(r2IntPtr);
		
		final Register temp = nextRegister("i32");
        StringBuilder instr = new StringBuilder();
        instr.append("\t");
		switch (kind) {
		case PLUSEXPR:
			instr.append(temp.name).append(" = add ");
			instr.append(r1Int.typeAndName()).append(", ").append(r2Int.name);
			break;
		case MINUSEXPR:
			instr.append(temp.name).append(" = sub ");
			instr.append(r1Int.typeAndName()).append(", ").append(r2Int.name);
			break;
		case MULTEXPR:
			instr.append(temp.name).append(" = mul ");
			instr.append(r1Int.typeAndName()).append(", ").append(r2Int.name);
			break;
		case DIVEXPR:
			instr.append(temp.name).append(" = sdiv ");
			instr.append(r1Int.typeAndName()).append(", ").append(r2Int.name);
			break;
		}
        o(instr.toString());
		
		final Register resultIntPtr = getElementPtr(resInst, "i32 *", 0, 1);
		store(temp, resultIntPtr);
		
		return result;
	}

	private List<Register> processMethodArgs(final Environment.CoolClass cls,
			final Register thiz, final Actuals args)
			throws CodeGenerationException, Environment.EnvironmentException {
        LinkedList<Register> l = new LinkedList<Register>();
        for (int i = 0; i < args.exprlist.size(); i++) {
            l.add(generate(cls, thiz, (Expr) args.exprlist.get(i)));
        }
        return l;
	}
	
	private void writeMainFunction() throws Environment.EnvironmentException,
			CodeGenerationException {
		o("define i32 @ll_main() {\n\tcall void @GC_init()\n");
        if (!env.class_map.containsKey("Main")) {
            System.err.println("\nWARNING: Main class not present");
        } 
        else {
            final Environment.CoolClass main = env.getClass("Main");
            final Register mainReg = instantiate(main);
            final Register mainInst = load(mainReg);
/*
            final Register mainMethodPtr = getElementPtr(new Register(main
                    .getInternalDescriptorName(), main.getInternalClassName()
                    + "*"), mainMethod.getInternalType() + "*", 0, mainMethod.index);
            final Register mainMethodInst = load(mainMethodPtr);
*/
            ClassDecl mainClass = (ClassDecl) main.node;
            ArrayList mainList = mainClass.classbody.featlist;
            int numBlockFeats = 0;
            for (int j = 0; j < mainList.size(); j++) {
                Feature feat = (Feature) mainList.get(j);
                if (feat.feattype.equals("block")) {
                    BlockFeature bf = feat.blockfeature;
                    numBlockFeats += 1;
                    Block b = bf.block;
                    //generate for the block
                    //Empty block
                    if (b.blockitems.size() == 0) {
                        System.err.println("Empty Block!!! All kinds of bad!");
                       //return new Register("null", ANY.getInternalInstanceName()
                        //    + "*"); //TODO is this ok?
                    }
                    else {
                        int num_registers = 0;
                        int num_locals = 0;
                        ArrayList<Register> registers = new ArrayList<Register>();
                        for (int i = 0; i < b.blockitems.size(); i++) {
                            BlockItem bi = (BlockItem) b.blockitems.get(i);
                            if (!bi.id.equals("")) {
                                num_locals++;
                                final String name = bi.id;
                                final Environment.CoolClass type = env
                                    .getClass(bi.type);
                                final Register letVar = instantiate(type);
                                env.local_types.push(bi.id, type);
                                if (bi.expr != null) {
                                    Register tmp = new Register(
                                            "mn" +String.valueOf(num_registers),
                                            "obj_Main*");
                                    final Register letValuePtr = generate(
                                            main, tmp, bi.expr);
                                    final Register letValue = load(letValuePtr);
                                    store(letValue, letVar);
                                }
                                log(MessageFormat.format("Pushing {0} for {1}", 
                                            letVar.typeAndName(), name));
                                env.registers.push(name, letVar);
                            }
                            Register tmp2 = new Register(
                                    "main" + String.valueOf(num_registers),
                                    "obj_Main*");
                            registers.add(generate(main, tmp2, bi.expr));
                            num_registers++;
                        }
                        for (int i = 0; i < num_locals; ++i) {
                            env.registers.pop();
                            env.local_types.pop();
                        }
                        //return registers.get(num_registers-1);
                    }
                }
            }
            log(MessageFormat.format(
                            "Main class has {0} blocks",numBlockFeats));
            if (numBlockFeats == 0) {
                System.err.println("No blocks in Main: BIG PROBLEM!");
            }

/*
            final Environment.CoolMethod mainMethod = env.lookupMethod(mainClass,
                    "main");
            final Register main = instantiate(mainClass);
            final Register mainInst = load(main);
            final Register mainMethodPtr = getElementPtr(new Register(mainClass
                    .getInternalDescriptorName(), mainClass.getInternalClassName()
                    + "*"), mainMethod.getInternalType() + "*", 0, mainMethod.index);
            final Register mainMethodInst = load(mainMethodPtr);
            // o("\t; ").append(mainMethodPtr.typeAndName()).append("\n");
            call(mainMethodInst, mainInst, mainMethod.type
                    .getInternalInstanceName()
                    + "*");
*/
            o("\tret i32 0\n}\n\n");
        }
	}
	
	private Register call(final Register methodPtr, final Register thiz,
			final String retType, final Register... args) {
		return call(methodPtr, thiz, retType, Arrays.asList(args));
	}
	
	private Register call(final Register methodPtr, final Register thiz,
			final String retType, final List<Register> args) {
		final Register call = nextRegister(retType);
        StringBuilder callstring = new StringBuilder();
        callstring.append("\t").append(call.name).append(" = call ");
        callstring.append(retType).append(" ").append(methodPtr.name);
        callstring.append("(").append(thiz.typeAndName());
		for (final Register r : args) {
			callstring.append(", ").append(r.typeAndName());
		}
        callstring.append(")");
		o(callstring.toString());
		return call;
	}

	private Register instantiate(final Environment.CoolClass cls)
			throws CodeGenerationException, Environment.EnvironmentException {
        StringBuilder inst1 = new StringBuilder(); 
		inst1.append("\t; START instantiating ").append(cls);
        o(inst1.toString());
		final Register result = nextRegister(cls.getInternalInstanceName()
				+ "**");
		alloca(result);
		malloc(result, result.derefType());
		final Register instance = load(result);
		o("\t; setting class pointer");
		final Register classPtr = getElementPtr(instance, cls
				.getInternalClassName()
				+ "**", 0, 0);
		final Register clazz = new Register(cls.getInternalDescriptorName(),
				cls.getInternalClassName() + "*");
		store(clazz, classPtr);
		int i = 1;
		for (final Environment.CoolAttribute a : cls.attr_list) {
            StringBuilder inst2 = new StringBuilder(); 
			inst2.append("\t; START attribute ").append(a).append(" of ")
					.append(cls);
            o(inst2.toString());
			final Register attrPtr = getElementPtr(instance, a.type
					.getInternalInstanceName()
					+ "**", 0, i);
			Register attrClass;
			if (a.type == STRING || a.type == INT || a.type == BOOLEAN) 
            {
				attrClass = instantiate(a.type);
				final Register attrInst = load(attrClass);
				store(attrInst, attrPtr);
			} 
            else 
            {
				store(new Register("null", a.type.getInternalInstanceName()	+ "*"), attrPtr);
			}
			i++;
            StringBuilder inst3 = new StringBuilder(); 
			inst3.append("\t; END attribute ").append(a).append(" of ")
					.append(cls);
            o(inst3.toString());
		}
		
		if (cls.builtin) {
			if (cls == STRING) {
				o("\t; Setting new String to default (empty)");
				setString(instance, "");
			} 
            else if (cls == INT) {
				o("\t; Setting new Int to default (0)");
				setInt(instance, 0);
			} 
            else if (cls == BOOLEAN) {
				o("\t; Setting new Bool to default (false)");
				setBool(instance, false);
			}
		}
		
		int i2 = 1;
		for (final Environment.CoolAttribute a : cls.attr_list) {
			if (a.expr != null) {
                StringBuilder inst4 = new StringBuilder(); 
				inst4.append("\t; Initialize ").append(a).append(
						" to introduced value");
                o(inst4.toString());
				final Register attrPtr = getElementPtr(instance, a.type
						.getInternalInstanceName()
						+ "**", 0, i2);
				final Register v = generate(cls, result, a.expr);
				Register attrInst = makeSinglePtr(v);
				if (!(attrInst.type + "*").equals(attrPtr.type)) 
                { 
                    attrInst = bitcast(attrInst, attrPtr.derefType());
				}
				store(attrInst, attrPtr);
			}
			i2++;
		}
		
        StringBuilder inst5 = new StringBuilder(); 
		inst5.append("\t; END instantiating ").append(cls);
        o(inst5.toString());
		
		return result;
	}
	
	public void setBool(final Register b, final boolean val) {
        String v;
        if (val) v = "true";
        else v = "false";
        comment(MessageFormat.format("Setting bool value to {0}", val));
		final Register boolPtr = getElementPtr(b, "i1 *", 0, 1);
		store(new Register(val ? "1" : "0", "i1"), boolPtr);
	}
	
	public void setInt(final Register i, final int val) {
		final Register intPtr = getElementPtr(i, "i32 *", 0, 1);
		store(new Register("" + val, "i32"), intPtr);
	}

	public void setString(final Register str, String val)
			throws CodeGenerationException {
		final int len = val.length() + 1;
		val = val.replaceAll("[\"]", "\\\\22");
		final Register lenPtr = getElementPtr(str, "i32 *", 0, 1);
		store(new Register("" + len, "i32"), lenPtr);
		final Register charPtr = getElementPtr(str, "i8 **", 0, 2);
		final Register charArrPtr = mallocCharArray(len);
        StringBuilder str1 = new StringBuilder(); 
		str1.append("\tstore ").append(charArrPtr.derefType()).append(" c\"")
				.append(val).append("\\00\", ")
				.append(charArrPtr.typeAndName());
        o(str1.toString());
		final Register castCharArrPtr = bitcast(charArrPtr, "i8 *");
		store(castCharArrPtr, charPtr);
	}

	private Register bitcast(final Register r, final String type) {
		final Register result = nextRegister(type);
        StringBuilder bitStr = new StringBuilder();
		bitStr.append("\t").append(result.name).append(" = bitcast ").append(
				r.typeAndName()).append(" to ").append(type);
        o(bitStr.toString());
		return result;
	}
	
	private void store(final Register value, final Register dest) {
        StringBuilder storeStr = new StringBuilder();
		storeStr.append("\tstore ").append(value.typeAndName()).append(", ")
				.append(dest.typeAndName());
        o(storeStr.toString());
	}
	
	private Register getElementPtr(final Register r, final String type,
			final int... args) {
		final Register result = nextRegister(type);
        StringBuilder gepStr = new StringBuilder();
		gepStr.append("\t").append(result.name).append(" = getelementptr ")
				.append(r.typeAndName());
		for (final int i : args) {
			gepStr.append(", ");
			gepStr.append("i32 ").append(i);
		}
		o(gepStr.toString());
		return result;
	}
	
	private Register alloca(final Register r) throws CodeGenerationException {
        StringBuilder allStr = new StringBuilder();
	    allStr.append("\t").append(r.name).append(" = alloca ").append(
				r.derefType());
        o(allStr.toString());
		return r;
	}
	
	private Register load(final Register from) throws CodeGenerationException {
		final Register result = nextRegister(from.derefType());
        StringBuilder loadStr = new StringBuilder();
		loadStr.append("\t").append(result.name).append(" = load ").append(
				from.typeAndName());
        o(loadStr.toString());
		return result;
	}
	
	private Register mallocCharArray(final int len) {
		final Register charArr = nextRegister("[" + len + " x i8]*");
		
		final Register call = nextRegister("i8 *");
        StringBuilder mallStr = new StringBuilder();
		mallStr.append("\t").append(call.name).append(
				" = call noalias i8* @GC_malloc(i64 ").append(len)
				.append(")");
        o(mallStr.toString());
		
        StringBuilder mallStr1 = new StringBuilder();
		mallStr1.append("\t").append(charArr.name).append(" = bitcast ").append(
				call.typeAndName()).append(" to ").append(charArr.type);
        o(mallStr1.toString());
		return charArr;
	}
	
	private Register malloc(final Register r, final String type) {
		final Register size = nextRegister(type);
		final Register cast = nextRegister("i64");
        StringBuilder mallStr = new StringBuilder();
		mallStr.append("\t").append(size.name).append(" = getelementptr ")
				.append(size.type).append(" null, i32 1\n");
		mallStr.append("\t").append(cast.name).append(" = ptrtoint ").append(
				type).append(" ").append(size.name).append(" to ").append(
				cast.type).append("\n");
		
		final Register call = nextRegister("i8 *");
		mallStr.append("\t").append(call.name).append(
				" = call noalias i8* @GC_malloc(i64 ").append(cast.name)
				.append(")\n");
		
		final Register cast2 = nextRegister(type);
		mallStr.append("\t").append(cast2.name).append(" = bitcast ").append(
				call.typeAndName()).append(" to ").append(cast2.type).append(
				"\n");
		
		mallStr.append("\tstore ").append(cast2.typeAndName()).append(", ")
				.append(r.type).append(" ").append(r.name);
        o(mallStr.toString());
		return r;
	}
}
