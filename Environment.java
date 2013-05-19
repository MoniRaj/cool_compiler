/**
 *  Environment.java
 *
 *  Contains compilation environment for Cool programming language,
 *  including Class, Method, and Attribute. Initializing an Environment
 *  builds the default classes (IO, String, Integer, etc).
 *
 *  @author: Nick Chaimov (nchaimov@uoregon.edu)
 *  @date: Winter 2010
 *
 *  Modified by: Paul Elliott and Monisha Balireddi (Spr 2013)
 *
 */

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import beaver.*;
import ast.*;


public class Environment {

    public static class CoolAttribute {
        public String name;
        public CoolClass type;
        public Node node;
        public CoolClass owner;

        public int index = -1;

        public CoolAttribute(final String name, final CoolClass type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return MessageFormat.format("{0}:{1}", name, type);
        }
    }

    public static class CoolClass {
        public String name;
        public CoolClass parent;
        public HashMap<String, CoolMethod> methods = new HashMap<String, CoolMethod>();
        public List<CoolMethod> method_list = new LinkedList<CoolMethod>();
        public HashMap<String, CoolAttribute> attributes = new HashMap<String, CoolAttribute>();
        public List<CoolAttribute> attr_list = new LinkedList<CoolAttribute>();
        public Node node;
        public boolean builtin = false;
        public boolean attr_inherit_done = false;
        public boolean method_inherit_done = false;

        public CoolClass(final String name) {
            this(name, null);
        }

        public CoolClass(final String name, final CoolClass parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getInternalClassName() {
            return "%__class_" + name;
        }

        public String getInternalInstanceName() {
            return "%__instance_" + name;
        }

        public String getInternalDescriptorName() {
            return "@_" + name;
        }
    }

    public static class CoolMethod {
        public String name;
        public List<CoolAttribute> arguments = new LinkedList<CoolAttribute>();
        public CoolClass type;
        public Node node;
        public CoolClass owner;
        public String builtin_implementation = null;

        public int index = -1;

        public CoolMethod(final String name, final CoolClass type) {
            this.name = name;
            this.type = type;
        }

        public String getInternalType() {
            final StringBuilder sb = new StringBuilder();
            sb.append(type.getInternalInstanceName());
            sb.append("* (").append(parent.getInternalInstanceName()).append(
                    " *");
            for (final CoolAttribute arg : arguments) {
                sb.append(", ");
                sb.append(arg.type.getInternalInstanceName());
                sb.append("* ");
            }
            sb.append(") *");
            return sb.toString();
        }

        public String getInternalName() {
            return "@__method_" + parent + "_" + name;
        }

        public String getName() {
            final StringBuilder sb = new StringBuilder();
            sb.append(name);
            for (final CoolAttribute c : arguments) {
                sb.append('*');
                sb.append(c.type.name);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(name).append('(');
            boolean first = true;
            for (final CoolAttribute c : arguments) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(c.name);
                sb.append(":");
                sb.append(c.type.name);
            }
            sb.append("):").append(type);
            return sb.toString();
        }
    }

    public static class EnvironmentException extends Exception {
        public EnvironmentException(final String msg) {
            super(msg);
        }
    }

    public boolean debug;

    public HashMap<String, CoolClass> class_map = new HashMap<String, CoolClass>();

    public HashStack<String, CoolClass> local_types = new HashStack<String, CoolClass>();
   // public HashStack<String, CodeGenerator.Register> registers = new HashStack<String, CodeGenerator.Register>(); //PE not needed yet

    public Environment() throws EnvironmentException {
        this(false);
    }

    public Environment(final boolean debug) throws EnvironmentException {

        this.debug = debug;

        log("Setting up default environment...");
        // Set up default classes
        final CoolClass any_class = new CoolClass("Any");
        any_class.parent = any_class;
        final CoolClass io_class = new CoolClass("IO", any_class);
        final CoolClass arrayany_class = new CoolClass("ArrayAny", any_class);
        final CoolClass symbol_class = new CoolClass("Symbol", any_class);
        final CoolClass unit_class = new CoolClass("Unit", any_class);
        final CoolClass int_class = new CoolClass("Int", any_class);
        final CoolClass string_class = new CoolClass("String", any_class);
        final CoolClass boolean_class = new CoolClass("Boolean", any_class);

        any_class.builtin = true;
        arrayany_class.builtin = true;
        symbol_class.builtin = true;
        unit_class.builtin = true;
        io_class.builtin = true;
        int_class.builtin = true;
        string_class.builtin = true;
        boolean_class.builtin = true;

        addClass(any_class);
        addClass(arrayany_class);
        addClass(unit_class);
        addClass(symbol_class);
        addClass(io_class);
        addClass(int_class);
        addClass(string_class);
        addClass(boolean_class);

        // Built-in methods of Object
        //final CoolMethod abort = new CoolMethod("abort", any_class);
        //final CoolMethod typeName = new CoolMethod("type_name", string_class);
        // TODO Change this if we ever implement SELF_TYPE
        //final CoolMethod copy = new CoolMethod("copy", any_class);

        //addMethod(any_class, abort);
        //addMethod(any_class, typeName);
        //addMethod(any_class, copy);

        // Built-in methods of IO
        // is_null
        //final CoolMethod outString = new CoolMethod("out_string", any_class);
        //outString.arguments.add(new CoolAttribute("x", string_class));
        //final CoolMethod outInt = new CoolMethod("out_int", any_class);
        //outInt.arguments.add(new CoolAttribute("x", int_class));
        //final CoolMethod inString = new CoolMethod("in_string", string_class);
        //final CoolMethod inInt = new CoolMethod("in_int", int_class);

        //addMethod(io_class, outString);
        //addMethod(io_class, outInt);
        //addMethod(io_class, inString);
        //addMethod(io_class, inInt);

        // Int has no built-in methods

        // Built-in methods of String
        //final CoolMethod length = new CoolMethod("length", int_class);
        //final CoolMethod concat = new CoolMethod("concat", string_class);
        //concat.arguments.add(new CoolAttribute("s", string_class));
        //final CoolMethod substr = new CoolMethod("substr", string_class);
        //substr.arguments.add(new CoolAttribute("i", int_class));
        //substr.arguments.add(new CoolAttribute("l", int_class));

        //addMethod(string_class, length);
        //addMethod(string_class, concat);
        //addMethod(string_class, substr);

        // final CoolAttribute test = new CoolAttribute("lols", any_class);
        // addAttribute(string_class, test);

        // Bool has no built-in method

        /*outString.builtin_implementation = "\t%v1.addr = alloca %__instance_String *\n"
                + "\tstore %__instance_String * %v1, %__instance_String ** %v1.addr\n"
                + "\t%tmp = load %__instance_String** %v1.addr\n"
                + "\t%tmp1 = getelementptr inbounds %__instance_String * %tmp, i32 0, i32 2\n"
                + "\t%tmp2 = load i8** %tmp1\n"
                + "\t%call = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @str.format, i32 0, i32 0), i8* %tmp2)\n"
                + "\t%retval = bitcast %__instance_IO * %this to %__instance_Object *\n"
                + "\tret %__instance_Object * %retval";

        outInt.builtin_implementation = "\t%v1.addr = alloca %__instance_Int *\n"
                + "\tstore %__instance_Int * %v1, %__instance_Int ** %v1.addr\n"
                + "\t%tmp = load %__instance_Int** %v1.addr\n"
                + "\t%tmp1 = getelementptr inbounds %__instance_Int * %tmp, i32 0, i32 1\n"
                + "\t%tmp2 = load i32* %tmp1\n"
                + "\t%call = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @str.format2, i32 0, i32 0), i32 %tmp2)\n"
                + "\t%retval = bitcast %__instance_IO * %this to %__instance_Object *\n"
                + "\tret %__instance_Object * %retval";
        */
        log("Done setting up default environment");

    }

    public void addAttribute(final CoolClass c, final CoolAttribute m)
            throws EnvironmentException {
        if (c.attributes.containsKey(m.name)) { //check if attribute already def'd
            throw new EnvironmentException(MessageFormat.format(
                    "Attempting to define attribute already defined: {0} (in class {1})", m, c));
        }
        CoolClass parent = c.parent;
        while (parent != getClass("Any")) { //check if attr already def'd in parent
            if (parent.attributes.containsKey(m.name)) { 
                throw new EnvironmentException(MessageFormat.format(
                        "Attempting to define attribute {0} in class {1}, but already defined in a superclass {2}", m.name, c, parent));
            }
            parent = parent.parent;
        }
        log(MessageFormat.format("Adding attribute {0} to class {1}", m, c));
        m.parent = c;
        c.attributes.put(m.name, m);
    }

    public void addClass(final CoolClass c) throws EnvironmentException {
        if (class_map.containsKey(c.name)) {
            throw new EnvironmentException(MessageFormat.format(
                    "Attempting to define class already defined: {0}", c));
        }
        log(MessageFormat.format("Adding class {0}", c));
        class_map.put(c.name, c);
    }

    public void addMethod(final CoolClass c, final CoolMethod m)
            throws EnvironmentException {
        if (c.methods.containsKey(m.name)) {
            throw new EnvironmentException(MessageFormat.format(
                    "Attempting to define method already defined: {0} (in class {1})", m, c));
        }
        for (final CoolAttribute a : m.arguments) { //check for illegal self arg
            if (a.name.equals("self")) {
                throw new EnvironmentException(
                        "The reserved name 'self' cannot be used as the name of a method parameter");
            }
        }

        CoolClass parent = c.parent;
        while (parent != getClass("Any")) {
            if (parent.methods.containsKey(m.name)) {
                //must have same #args, same types, same return type
                final CoolMethod m2 = parent.methods.get(m.name);
                if (m.arguments.size() != m2.arguments.size()) {
                    throw new EnvironmentException(MessageFormat.format(
                            "Attempting to create overriding method {0} with different number of arguments from overriden method {1}", m, m2));
                }
                final Iterator<CoolAttribute> iter1 = m.arguments.iterator();
                final Iterator<CoolAttribute> iter2 = m2.arguments.iterator();
                while (iter1.hasNext() && iter2.hasNext()) {
                    if ((iter1.next().type != iter2.next().type)
                            || (m.type != m2.type)) {
                        throw new EnvironmentException(MessageFormat.format(
                                "Attempting to override method {0} with method of different signature {1}.", m2, m));
                    }
                }
            }
            parent = parent.parent;
        }
        log(MessageFormat.format("Adding method {0} to class {1} ({2})", m, c,
                m.getName()));
        m.owner = c;
        c.methods.put(m.name, m);
    }

    public CoolClass getClass(final String name) throws EnvironmentException {
        final CoolClass result = class_map.get(name);
        if (result == null) {
            throw new EnvironmentException(MessageFormat.format(
                    "Class {0} is not defined.", name));
        }
        return result;
    }

    public Environment.CoolMethod lookupMethod(Environment.CoolClass cls,
            final String id) throws EnvironmentException {
        Environment.CoolMethod result = cls.methods.get(id);
        final CoolClass ANY = getClass("Any");
        while (result == null && cls != ANY) { //check parents if need be
            log(MessageFormat.format("Method {2} not found in {0}; trying {1}",
                    cls, cls.parent, id));
            cls = cls.parent;
            result = cls.methods.get(id);
        }
        if (result == null) {
            log(MessageFormat.format("Method {0} not found", id));
        } else {
            log(MessageFormat.format("Method {0} found in {1}", id, cls));
        }
        return result;
    }

    public Environment.CoolClass lookupAttrType(Environment.CoolClass cls,
            final String id) throws EnvironmentException {
        final CoolClass ANY = getClass("Any");
        if (id.equals("self")) {
            log(MessageFormat.format("SELF is of type {0}", cls));
            return cls;
        }
        log(MessageFormat.format(
                "Looking up attribute {0} in local environment", id));
        Environment.CoolClass result = local_types.get(id);
        if (result == null) {
            log(MessageFormat.format(
                    "Looking up attribute {0} in current class {1}", id, cls));
            if (cls.attributes.get(id) != null) {
                result = cls.attributes.get(id).type;
            }
        } else {
            log(MessageFormat.format(
                    "Attribute {0} found in local environment: ", id, result));
            return result;
        }
        while (result == null && cls != ANY) {
            cls = cls.parent;
            log(MessageFormat.format("Looking up attribute {0} in class {1}",
                    id, cls));
            if (cls.attributes.get(id) != null) {
                result = cls.attributes.get(id).type;
            }
        }
        if (result == null) {
            log(MessageFormat.format("Attribute {0} not found", id));
            throw new EnvironmentException(MessageFormat.format(
                    "Attribute {0} referenced but not defined", id));
        } else {
            log(MessageFormat.format("Attribute {0} found in class {1}: {2}",
                    id, cls, result));
        }

        return result;
    }

    protected void log(final String msg) {
        if (debug) {
            System.err.println(msg);
        }
    }

}
