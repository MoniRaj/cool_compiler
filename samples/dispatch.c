/*
 * 
 * Simulating object-oriented dynamic dispatch in C. 
 * 
 *  To test this (on a unixoid; probably works similarly with cygwin on windows):
 *    Edit Makefile to find llvm tools where you put them. 
 *    Then: 
 *       make dispatch
 *       ./dispatch
 *
 */
#include <stdlib.h>


/*  Simulating this: 
   Class single { 
     int x = 0;  
     void incx() { this.x += 1; }  
     int getx() { return x; } 
   }
*/


/* The object structure 
   contains instance variables ("attributes" in Cool)
   and a pointer to its class. 
*/
struct single_obj {
  struct single_class* class_ptr;
  int x;
};

/* The class structure 
   contains a pointer to its superclass (omitted here)
   and the "vtable" structure of method pointers. 
*/ 
struct single_class {
  int class_id;  /* Only if this makes the "case" dispatch easier. */ 
  /* Super ptr should go here */
  void (*incx) (struct single_obj* );   /* First entry in vtable */ 
  int  (*getx) (struct single_obj* );   /* Second entry in vtable */ 
}; 


/* The methods, each with an implicit "this" argument */ 

void method_incx(struct single_obj *this) {  
  this->x = this->x + 1;
}

int method_getx(struct single_obj *this) {
  return this->x;
}

/* 
 * Now we'll create a dummy ll_main program that creates one single_class object
 * (along with its class) and calls its two methods. 
 */ 

int ll_main(int junk) {
  /* Create the class object (in the stack frame, in this case) */
  struct single_class the_class;
  the_class.incx = method_incx;
  the_class.getx = method_getx;
  /* Create an object of that class (in the heap) */ 
  struct single_obj* the_obj = (struct single_obj*) 
    malloc(sizeof(struct single_obj));
  /* Initialize it */ 
  the_obj->class_ptr = &the_class;
  the_obj->x = 0; 

  /* OK, we've got an object with methods.
     Let's do a dynamic dispatch through the vtable.  
     First, call "the_obj.incx()". 
  */
  
  /* Get the class from the object */ 
  struct single_class *vtable = the_obj->class_ptr;

  /* Get the method pointer from the class */ 
  void (*method) (struct single_obj*) = vtable->incx;

  /* Call the method, passing the object as "this" or "self" */ 
  (*method)(the_obj);

  /* Now the same thing, but with method getx().  We've got the vtable already, 
     so let's compress it down to fewer steps. 
  */ 
  int (*meth2) (struct single_obj*) = vtable->getx;
  int result = (*meth2)(the_obj);

  return result; 
}
     	




  

  
