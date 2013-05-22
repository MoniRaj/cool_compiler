/**
 *
 * TreeWalker.java
 *
 * A walker that traverses the AST and handles typechecking.
 * Checks include:
 *      1. identify classes
 *      2. determine inheritance hierarchy and check cycles
 *      3. identify attributes and methods
 *      4. check attribute inheritance
 *      5. check method inheritance
 *      6. typecheck attributes
 *      7. typecheck methods
 *
 * Much of this work is inspired (or directly written) by 
 *      Nick Chaimov (nchaimov@uoregon.edu), Winter 2010
 *
 * Modified by: Paul Elliott and Monisha Balireddi (Spr 2013)
 *
 */

package ast;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import beaver.*;

public class TreeWalker {
     
    public static class TypeCheckException extends Exception {

        public TypeCheckException(final String msg) {
            super(msg);
        }
    }

    protected Environment env;
    protected boolean debug;

    protected Environment.CoolClass CURR_CLASS;

    protected final Environment.CoolClass ANY;
    protected final Environment.CoolClass UNIT;
    protected final Environment.CoolClass ARRAYANY;
    protected final Environment.CoolClass SYMBOL;
    protected final Environment.CoolClass BOOLEAN;
    protected final Environment.CoolClass INT;
    protected final Environment.CoolClass STRING;
    protected final Environment.CoolClass IO;
        
    public TreeWalker(final Node root, final boolean debug)
            throws Environment.EnvironmentException {
        this.root = root;
        this.debug = debug;
        env = new Environment(debug);

        CURR_CLASS = null;

        ANY = env.getClass("Any");
        ARRAYANY = env.getClass("ArrayAny");
        BOOLEAN = env.getClass("Boolean");
        UNIT = env.getClass("Unit");
        SYMBOL = env.getClass("Symbol");
        INT = env.getClass("Int");
        STRING = env.getClass("String");
        IO = env.getClass("IO");
    }

/*
Helper Methods
*/
    
    //TODO check that these helper methods work correctly

    protected void addMethod(MethodFormal mf) {
        final Environment.CoolClass return_type = env.getClass(
                mf.type);
        final Environment.CoolMethod method = new Environment.CoolMethod(
                mf.id, return_type);
        method.node = mf;
        processMethodArguments(method, mf.varformals);
        env.addMethod(CURR_CLASS, method);
        //node.type = return_type; //TODO do we need this?
    }

    protected void addAttribute(String id, String type) {
        final Environment.CoolClass type = env.getClass(vf.type);
        final Environment.CoolAttribute attr = new Environment.CoolAttribute(
                vf.id, type);
        attr.node = vf; //TODO check that this works
        env.addAttribute(CURR_CLASS, attr);
        //node.type = type; //TODO do we need this?
    }

    protected void processMethodArguments(final Environment.CoolMethod method,
            final Node methodvf) throws Environment.EnvironmentException,
            TypeCheckException {
        for (int i = 0; i < methodvf.formalvarlist.size(); i++) {
            MethodFormal mf = (MethodFormal) methodvf.formalvarlist.get(i);
            method.arguments.add(new Environment.CoolAttribute(mf.id, mf.type));
        }
    }

    protected void inheritAttributes(final Environment.CoolClass c) {
        if (!c.attrInheritDone && c != ANY) {
            log("Inheriting attributes for " + c);
            inheritAttributes(c.parent);
            final LinkedList<Environment.CoolClass> q = 
                    new LinkedList<Environment.CoolClass>();
            q.push(c);
            Environment.CoolClass p = c.parent;
            while (p != ANY) {
                q.push(p);
                p = p.parent;
            }
            while (!q.isEmpty()) {
                final Environment.CoolClass curr_class = q.pop();
                for (final Environment.CoolAttribute a : curr_class.attributes
                        .values()) {
                    log("Found attribute " + a + " of " + curr_class + " for "
                            + c);
                    c.attrList.add(a);
                }
            }
            c.attrInheritDone = true;
        }
        log("Attribute inheritance complete for class: " + c);
        if (debug) {
            for (final Environment.CoolAttribute a : c.attrList) {
                System.err.println(MessageFormat.format("In {0} is {1}", c, a));
            }
        }
    }

    protected void inheritMethods(final Environment.CoolClass c) {
        if (!c.methodInheritDone && c != ANY) {
            log("Inheriting methods for " + c);
            inheritMethods(c.parent);
            final LinkedList<Environment.CoolClass> q = 
                    new LinkedList<Environment.CoolClass>();
            q.push(c);
            Environment.CoolClass p = c.parent;
            while (p != ANY) {
                q.push(p);
                p = p.parent;
            }
            while (!q.isEmpty()) {
                final Environment.CoolClass curr_class = q.pop();
                for (final Environment.CoolMethod a : curr_class.methods.values()) {
                    log("Found method " + a + " of " + curr_class + " for " + c);
                    final Environment.CoolMethod overriddenMethod = c.methods
                            .get(a.name);
                    log(overriddenMethod != null ? "" + overriddenMethod.parent
                            : "not overridden");
                    if (overriddenMethod != null) {
                        if (!c.methodList.contains(overriddenMethod)) {
                            c.methodList.add(overriddenMethod);
                        }
                    } else {
                        if (!c.methodList.contains(a)) {
                            c.methodList.add(a);
                        }
                    }
                }
            }
            c.methodInheritDone = true;
        }
        log("Method inheritance complete for class: " + c);
        if (debug) {
            for (final Environment.CoolAttribute a : c.attrList) {
                System.err.println(MessageFormat.format("In {0} is {1}", c, a));
            }
        }
    }

    public void checkAttributes() throws Environment.EnvironmentException,
           TypeCheckException {
//TODO FIX
        for (final Entry<String, Environment.CoolClass> e : 
                env.class_map.entrySet()) {
            final Environment.CoolClass curr_class = e.getValue();
            if (curr_class.builtin) {
                continue;
            }
            log(MessageFormat.format("Typechecking attributes of class {0}",
                    curr_class));
            for (final Entry<String, Environment.CoolAttribute> e2 : 
                    curr_class.attributes.entrySet()) {
                final Environment.CoolAttribute attr = e2.getValue();
                if (attr.node.right != null) {
                    log("Checking attribute " + attr);
                    check(curr_class, attr.node.right); //TODO fix, implement, chk
                    log(MessageFormat.format("Expr type: {0}; Attr type: {1}",
                            attr.node.right.type, attr.node.type));
                    if (!moreGeneralOrEqualTo(attr.node.type,
                            attr.node.right.type)) { //TODO fix, implement, check
                        throw new TypeCheckException(MessageFormat.format(
                                "Attribute {0} has value of wrong type: {1}",
                                attr, attr.node.right.type)); //TODO fix
                    }
                }
            }
        }
    }

    public void checkMethods() throws Environment.EnvironmentException,
            TypeCheckException {
//TODO FIX
        for (final Entry<String, Environment.CoolClass> e : env.class_map
                .entrySet()) {
            final Environment.CoolClass curr_class = e.getValue();
            if (curr_class.builtin) {
                continue;
            }
            log(MessageFormat.format("Typechecking methods of class {0}",
                    curr_class));
            for (final Entry<String, Environment.CoolMethod> e2 : curr_class.methods
                    .entrySet()) {
                final Environment.CoolMethod method = e2.getValue();
                if (method.node.right != null) {
                    log("Checking method " + method);
                    for (final Environment.CoolAttribute a : method.arguments) {
                        log(MessageFormat
                                .format(
                                        "Pushing method argument {0} onto local environment",
                                        a));
                        env.localTypes.push(a.name, a.type);
                    }
                    log(MessageFormat.format("Local environment is {0}",
                            env.localTypes));
                    check(curr_class, method.node.right);
                    for (@SuppressWarnings("unused")
                    final Environment.CoolAttribute a : method.arguments) {
                        log("Popping local environment");
                        env.localTypes.pop();
                    }
                    log(MessageFormat.format("Local environment is {0}",
                            env.localTypes));
                    log(MessageFormat.format(
                            "Declared method type: {0}; Method body type: {1}",
                            method.node.right.type, method.node.type));
                    if (!moreGeneralOrEqualTo(method.node.type,
                            method.node.right.type)) {
                        throw new TypeCheckException(MessageFormat.format(
                                "Method {0} has body of wrong type: {1}",
                                method, method.node.right.type));
                    }
                }
            }
        }
    }

    public Environment.CoolClass check(final Environment.CoolClass curr_class,
            final Node node) throws Environment.EnvironmentException,
            TypeCheckException {
//TODO FIX
        if (node != null) {
            switch (node.kind) {

            // LITERALS
            case Terminals.TRUE:
            case Terminals.FALSE:
                return setType(BOOLEAN, node);
            case Terminals.INTLIT:
                return setType(INT, node);
            case Terminals.STRINGLIT:
                return setType(STRING, node);

                // IDENTIFIER
            case Terminals.ID:
                return setType(env
                        .lookupAttrType(curr_class, (String) node.value), node);

                // OPERATORS
            case Terminals.ASSIGN: {
                if (node.left.kind != Terminals.ID) {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Left-hand side of an assignment must be an identifier, but {0} found instead",
                                            Util.idToName(node.left.kind)));
                }
                if (node.left.value.equals("self")) {
                    throw new TypeCheckException(
                            "The special variable 'self' cannot be assigned to.");
                }
                final Environment.CoolClass leftType = check(curr_class,
                        node.left);
                final Environment.CoolClass rightType = check(curr_class,
                        node.right);
                log(MessageFormat
                        .format(
                                "Assignment: Left-side {0} has type {1}; right-side has type {2}",
                                node.left.value, node.left.type,
                                node.right.type));
                if (moreGeneralOrEqualTo(leftType, rightType)) {
                    log(MessageFormat.format(
                            "Most specific parent in common is {0}",
                            mostSpecificParent(leftType, rightType)));
                    return setType(rightType, node);
                } else {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Expression of type {0} not compatible with variable type {1}",
                                            node.right.type, node.left.type));
                }
            }

            case Terminals.NEW: {
                return setType(env.getClass((String) node.value), node);
            }

            case Terminals.DOT: {
                typecheckMethodArguments(curr_class, node.right);
                Environment.CoolClass containingClass;
                if (node.left != null) {
                    check(curr_class, node.left);
                    containingClass = node.left.type;
                } else {
                    containingClass = curr_class;
                }

                if (node.center != null) {
                    if (node.center.kind != Terminals.TYPEID) {
                        throw new TypeCheckException(
                                MessageFormat
                                        .format(
                                                "Malformed AST; center node of DOT, if it exists, should be TYPEID, but it was {0}",
                                                Util.idToName(node.center.kind)));
                    }
                    final Environment.CoolClass staticClass = env
                            .getClass((String) node.center.value);
                    if (!moreGeneralOrEqualTo(staticClass, containingClass)) {
                        throw new TypeCheckException(
                                MessageFormat
                                        .format(
                                                "Static class {0} not compatible with type ({1}) of {2}",
                                                staticClass, containingClass,
                                                node.left.value));
                    }
                    log(MessageFormat
                            .format(
                                    "Static dispatch; will use {0} as type for method call {1}",
                                    staticClass, node.value));
                    containingClass = staticClass;
                }

                log(MessageFormat.format("Looking up method {0} in {1}",
                        node.value, containingClass));
                final Environment.CoolMethod method = env.lookupMethod(
                        containingClass, (String) node.value);
                if (method == null) {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Tried to call method {0} in {1}, but method not found.",
                                            node.value, containingClass));
                }

                final List<Environment.CoolClass> actuals = new LinkedList<Environment.CoolClass>();
                getArgumentTypes(node.right, actuals);
                final List<Environment.CoolAttribute> formals = method.arguments;

                if (actuals.size() != formals.size()) {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Call to method {0} has wrong number of arguments (expected {1}, found {2})",
                                            method, formals.size(), actuals
                                                    .size()));
                }

                final Iterator<Environment.CoolClass> actualIter = actuals
                        .iterator();
                final Iterator<Environment.CoolAttribute> formalIter = formals
                        .iterator();

                while (actualIter.hasNext() && formalIter.hasNext()) {
                    final Environment.CoolClass expectedType = formalIter
                            .next().type;
                    final Environment.CoolClass actualType = actualIter.next();

                    if (!moreGeneralOrEqualTo(expectedType, actualType)) {
                        throw new TypeCheckException(MessageFormat.format(
                                "Expected argument of type {0}, but found {1}",
                                expectedType, actualType));
                    }
                }

                return setType(method.type, node);
            }

            case Terminals.IF: {
                check(curr_class, node.left);
                if (node.left.type != BOOLEAN) {
                    throw new TypeCheckException(MessageFormat.format(
                            "If condition must be of type Bool, but {0} found",
                            node.left.type));
                }
                check(curr_class, node.center);
                check(curr_class, node.right);
                final Environment.CoolClass unionType = mostSpecificParent(
                        node.center.type, node.right.type);
                log(MessageFormat.format(
                        "Then type: {0}; Else type: {1}; Union type: {2}",
                        node.center.type, node.right.type, unionType));
                return setType(unionType, node);
            }

            case Terminals.SEMI: {
                // Check the mandatory first expression
                check(curr_class, node.left);
                Environment.CoolClass lastType = node.left.type;

                // Then check the optional remaining expressions,
                // if they are present.
                if (node.right != null) {
                    lastType = checkSequence(curr_class, node.right);
                }
                return setType(lastType, node);
            }

            case Terminals.LET: {
                final int numVars = addLetIntroductions(curr_class, node.left, 0);
                log(MessageFormat
                        .format(
                                "Let expression resulted in {0} variables added to local environment, which is now: {1}",
                                numVars, env.localTypes));
                check(curr_class, node.right);
                for (int i = 0; i < numVars; ++i) {
                    log("Popping mapping off local environment");
                    env.localTypes.pop();
                }
                log(MessageFormat.format(
                        "After let evaluated, local environment is {0}",
                        env.localTypes));
                return setType(node.right.type, node);
            }

            case Terminals.CASE: {
                check(curr_class, node.left);
                List<Environment.CoolClass> list = new LinkedList<Environment.CoolClass>();
                list = getCaseTypes(curr_class, node.right, list);
                final Iterator<Environment.CoolClass> iter = list.iterator();
                Environment.CoolClass caseClass = iter.next();
                while (iter.hasNext()) {
                    final Environment.CoolClass nextClass = iter.next();
                    log(MessageFormat.format("Comparing {0} and {1}",
                            caseClass, nextClass));
                    caseClass = mostSpecificParent(caseClass, nextClass);
                }
                log(MessageFormat.format("Union type of case statement is {0}",
                        caseClass));
                return setType(caseClass, node);
            }

            case Terminals.WHILE: {
                check(curr_class, node.left);
                if (node.left.type != BOOLEAN) {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Loop condition of a WHILE loop must be a Bool, but found {0}",
                                            node.left.type));
                }
                check(curr_class, node.right);
                return setType(ANY, node);
            }

            case Terminals.ISVOID: {
                check(curr_class, node.left);
                return setType(BOOLEAN, node);
            }

            case Terminals.NOT: {
                check(curr_class, node.left);
                if (node.left.type != BOOLEAN) {
                    throw new TypeCheckException(MessageFormat.format(
                            "Argument to NOT must be Bool, but found {0}",
                            node.left.type));
                }
                return setType(BOOLEAN, node);
            }

            case Terminals.LT:
            case Terminals.LEQ: {
                check(curr_class, node.left);
                check(curr_class, node.right);
                if (node.left.type != INT) {
                    throw new TypeCheckException(
                            "Left argument of comparison must be Int, but found"
                                    + node.left.type);
                }
                if (node.right.type != INT) {
                    throw new TypeCheckException(
                            "Right argument of comparison must be Int, but found"
                                    + node.left.type);
                }
                return setType(BOOLEAN, node);
            }

            case Terminals.MINUS:
            case Terminals.DIV:
            case Terminals.TIMES:
            case Terminals.PLUS: {
                check(curr_class, node.left);
                check(curr_class, node.right);
                if (node.left.type != INT || node.right.type != INT) {
                    throw new TypeCheckException("The operator "
                            + Util.idToName(node.kind)
                            + " takes two arguments of type Int");
                }
                return setType(INT, node);
            }

            case Terminals.EQ: {
                check(curr_class, node.left);
                check(curr_class, node.right);

                if ((node.left.type == INT && node.right.type != INT)
                        || (node.left.type == BOOLEAN && node.right.type != BOOLEAN)
                        || (node.left.type == STRING && node.right.type != STRING)
                        || (node.right.type == INT && node.left.type != INT)
                        || (node.right.type == BOOLEAN && node.left.type != BOOLEAN)
                        || (node.right.type == STRING && node.left.type != STRING)) {
                    throw new TypeCheckException(
                            MessageFormat
                                    .format(
                                            "Ints, Bools and Strings can only be compared to each other, but tried to compare a {0} to a {1}",
                                            node.left.type, node.right.type));
                }

                return setType(BOOLEAN, node);
            }

            case Terminals.NEG: {
                check(curr_class, node.left);
                if (node.left.type != INT) {
                    throw new TypeCheckException(
                            "The ~ operator only takes objects of type Int, but found "
                                    + node.left.type);
                }
                return setType(INT, node);
            }

            default:
                throw new TypeCheckException("Unimplemented node type: "
                        + Util.idToName(node.kind));
            }
        }
        return null;
    }

/*
Visit Methods
*/
    public void visit(Program p) {
        print("{ ");
        print("\"Program\": { ");
        
        //1. Build class hierarchy
        for (int i = 0; i < p.classlist.size(); i++) {
            ClassDecl cls = p.classlist.get(i);
            final Environment.CoolClass new_class = new Environment.CoolClass(
                    cls.type);
            new_class.node = cls; //TODO make sure this works correctly
            env.addClass(new_class);
        }
        log("Added classes to class map");
       
        //2. Check class hierarchy
        for (int i = 0; i < p.classlist.size(); i++) 
        {
            try {
                String parent_type = p.classlist.get(i).extension.type;
                if ( parent_type != "") 
                {
                    final Environment.CoolClass this_class = env.getClass(
                            parent_type);
                    if (parent_type.equals("Int") || 
                        parent_type.equals("Boolean") || 
                        parent_type.equals("String")) 
                    {
                        throw new TypeCheckException(MessageFormat.format(
                                "Class {0} inherits from prohibited class {1}",
                                this_class, parent_type));
                    }
                    final Environment.CoolClass parent_class = env.getClass(
                            parent_type);
                    this_class.parent = parent_class;
                    log(MessageFormat.format(
                            "Class {0} inherits from {1}", 
                            this_class, parent_class));
                } 
                else 
                {
                    final Environment.CoolClass this_class = env.getClass(parent_type);
                    final Environment.CoolClass parent_class = ANY;
                    this_class.parent = parent_class;
                    log(MessageFormat.format(
                           "Class {0} inherits Any", this_class, parent_class));
                }   
            }
            catch(TypeCheckException e )
            { 
                log(MessageFormat.format("Type check error: {0}", e.message);
            }
        }
        log("Class hierarchy complete.");

        //3. Check hierarchy for cycles, tree-ify class hierarchy
        final HashSet<Environment.CoolClass> red = 
                new HashSet<Environment.CoolClass>();
        final HashSet<Environment.CoolClass> green = 
                new HashSet<Environment.CoolClass>();
        green.add(ANY);
        final Iterator<Entry<String, Environment.CoolClass>> it = env.class_map
                .entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, Environment.CoolClass> entry = it.next();
            Environment.CoolClass curr_class = entry.getValue();
            while (!green.contains(curr_class)) {
                if (red.contains(curr_class)) {
                    throw new TypeCheckException(
                            "Class hierarchy is not a tree.");
                }
                else {
                    red.add(curr_class);
                    //Create hierarchical class list for processing
                    p.class_hierarchy.add(0,red_class);
                    curr_class = curr_class.parent;
                }
            }
            final Iterator<Environment.CoolClass> reds = red.iterator();
            Environment.CoolClass red_class;
            while (reds.hasNext()) {
                red_class = reds.next();
                reds.remove();
                green.add(red_class);
            }
            red.clear();
        }
        log("Class hierarchy contains no cycles.");

        for (int i = 0; i < p.class_hierarchy.size(); i++) {
            print("\"class\": { ");
            CURR_CLASS = p.class_hierarchy.get(i);
            //4. Bind attributes and methods
            log("Binding attributes and methods: " + CURR_CLASS.name);
            CURR_CLASS.node.accept();
            print(" }");
            if (i < p.classlist.size()-1) print(", ");
            else print(" ");
        }

        //5. Typecheck attributes and methods
        checkAttributes();
        checkMethods();

        print(" } }");
    }

    public void visit(ClassDecl c) {
        print("\"type\": \"" + c.type + "\", \"varformals\": { ");
        visit(c.varformals);
        print(" },");
        if (!c.extension.type.equals("")) {
            print(" \"extends\": { ");
            visit(c.extension);
            print(" },");
        }
        print(" \"classbody\": { ");
        visit(c.classbody);
        //Inherit attributes and methods from parents
        inheritAttributes(env.getClass(c.type)); 
        inheritMethods(env.getClass(c.type));
        print(" } ");
    }
    
    public void visit(ClassVarFormals vf) {
        for (int i = 0; i < vf.formalvarlist.size(); i++) {
            print("\"var\": ");
            visit((ClassFormal) vf.formalvarlist.get(i));
            if (i < vf.formalvarlist.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(ClassFormal cv) {
        //Add class formal variable to class attributes
        addAttribute(cv.id, cv.type);
        print("\""+cv.id+":"+cv.type+"\"");
    }

    public void visit(Extension e) {
        if (e.isactuals) {
            print("\"type\": \""+e.type+"\",\"actuals\": { ");
            visit(e.actuals);
            print(" } ");
        }
        else {
            print("\"native\": \"native\"");
        }
    }
 
    public void visit(Actuals a) {
        for (int i = 0; i < a.exprlist.size(); i++) {
            print("\"expr\": { ");
            ((Expr) a.exprlist.get(i)).accept(this);
            print(" }");
            if (i < a.exprlist.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(ClassBody b) {
        for (int i = 0; i < b.featlist.size(); i++) {
            print("\"feature\": { ");
            visit((Feature) b.featlist.get(i));
            print(" }");
            if (i < b.featlist.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(Feature f) {
        if (f.feattype.equals("method")) {
            visit(f.methodfeature);
        }
        else if (f.feattype.equals("var")) {
            visit(f.varfeature);
        }
        else if (f.feattype.equals("block")) {
            visit(f.blockfeature);
        }
        else {
            System.out.println("SHOULD NOT HAPPEN!!");
        }
    }

    public void visit(MethodFeature mf) {
        print("\"def\": { ");
        print("\"id\": \"" + mf.override + mf.id + "\", \"formals\": { ");
        //Add method to class methods
        addMethod(mf);
        visit(mf.varformals);
        print(" }, \"type\": \"");
        if (mf.isnative) {
            print(mf.type+" = native\"");
        }
        else {
            print(mf.type+"\", \"expr\": { ");
            mf.expr.accept(this);
            print(" } ");
        }
        print(" } ");
    }

    public void visit(VarFeature vf) {
        print("\"var\": ");
        if (vf.isnative) {
            print("\""+vf.id+" = native\"");
        }
        else {
            //Add variable feature to class attributes
            addAttribute(vf.id, vf.type);
            print("{ \"id\": \"" + vf.id + "\", ");
            print("\"type\": \"" + vf.type + "\", ");
            print("\"expr\": { ");
            vf.expr.accept(this);
            print(" } } ");
        }
    }

    public void visit(BlockFeature bf) {
        print("\"block\": { ");
        visit(bf.block);
        print(" } ");
    }

    public void visit(MethodVarFormals vf) {
        for (int i = 0; i < vf.formalvarlist.size(); i++) {
            print("\"formal\": ");
            visit((MethodFormal) vf.formalvarlist.get(i));
            if (i < vf.formalvarlist.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(MethodFormal cv) {
        print("\"" + cv.id + " " + cv.type + "\"");
    }

    public void visit(Block bl) {
        for (int i = 0; i < bl.blockitems.size(); i++) {
            visit((BlockItem) bl.blockitems.get(i));
            if (i < bl.blockitems.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(BlockItem bi) {
        if (bi.id.equals("")) {
            print("\"expr\": { ");
            bi.expr.accept(this);
            print(" } ");
        }
        else {
            print("\"var\": { ");
            print("\"id\": \"" + bi.id + "\", \"type\": \"" + bi.type + "\", ");
            print("\"expr\": { ");
            bi.expr.accept(this);
            print(" } } ");
        }
    }

    public void visit(AssignExpr e) {
		print("\"id\": \"" + e.id + "\", \"op\": \"=\", \"expr\": { ");
        e.expr.accept(this);
        print(" } ");
	}

    public void visit(IfExpr e) {
		print("\"op\": \"if\", \"expr\": { ");
        e.expr1.accept(this);
        print(" }, \"expr\": { ");
        e.expr2.accept(this);
        print(" }, \"op\": \"else\", \"expr\": { ");
        e.expr3.accept(this);
        print(" } ");
	}

    public void visit(WhileExpr e) {
		print("\"op\": \"while\", \"expr\": { ");
        e.expr1.accept(this);
        print(" }, \"expr\": { ");
        e.expr2.accept(this);
        print(" } ");
	}

    public void visit(MatchExpr e) {
        print("\"expr\": { ");
        e.expr.accept(this);
        print(" }, \"op\": \"match\", \"cases\": { ");
        visit(e.cases);
        print(" } ");
	}

    public void visit(LeExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"<=\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}
    
    public void visit(LtExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"<\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(EqualsExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"==\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(PlusExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"+\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(MinusExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"-\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(MultExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"*\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(DivExpr e) {
        print("\"expr\": { ");
        e.l.accept(this);
        print(" }, \"op\": \"/\", \"expr\": { ");
        e.r.accept(this);
        print(" } ");
	}

    public void visit(NotExpr e) {
		print("\"op\": \"!\", \"expr\": { ");
        e.expr.accept(this);
        print(" } ");
	}
    
    public void visit(NegExpr e) {
		print("\"op\": \"-\", \"expr\": { ");
        e.expr.accept(this);
        print(" } ");
	}

    public void visit(DotExpr e) {
        print("\"expr\": { ");
        e.expr.accept(this);
        print(" }, \"id\": \"" + e.id + "\", \"actuals\": { ");
        visit(e.actuals);
        print(" } ");
    }

    public void visit(PrimaryExpr e) {
        if (e.primarytype.equals("supercall")) {
            print("\"id\": \"super." + e.id + "\", \"actuals\": { ");
            visit(e.actuals);
            print(" } ");
        }
        else if (e.primarytype.equals("call")) {     
            print("\"id\": \"" + e.id + "\", \"actuals\": { ");
            visit(e.actuals);
            print(" } ");
        }
        else if (e.primarytype.equals("new")) {         
            print("\"op\": \"new\", \"type\": \"" + e.type);
            print("\", \"actuals\": { ");
            visit(e.actuals);
            print(" } ");
        }
        else if (e.primarytype.equals("block")) {       
            print("\"block\": { ");
            visit(e.block);
            print(" } ");
        }
        else if (e.primarytype.equals("parenexpr")) {   
            print("\"expr\": { ");
            e.expr.accept(this);
            print(" } ");
        }
        else if (e.primarytype.equals("null")) {        
            print("\"op\": \"null\"");
        }
        else if (e.primarytype.equals("id")) {          
            print("\"id\": \"" + e.id + "\"");
        }
        else if (e.primarytype.equals("integer")) {     
            print("\"integer\": \"" + e.integer + "\"");
        }
        else if (e.primarytype.equals("string")) {      
            print("\"string\": \"" + e.string + "\"");
        }
        else if (e.primarytype.equals("boolean")) {     
            if (e.bool) print("\"boolean\": \"true\"");
            else print("\"boolean\": \"false\"");
        }
        else if (e.primarytype.equals("this")) {        
            print("\"op\": \"this\"");
        }
    }

    public void visit(Cases c) {
        for (int i = 0; i < c.caseslist.size(); i++) {
            print("\"case\": { ");
            visit((Case) c.caseslist.get(i));
            print(" }");
            if (i < c.caseslist.size()-1) print(", ");
            else print(" ");
        }
    }

    public void visit(Case c) {
        if (c.isnull) {
            print("\"val\": \"null\", ");
        }
        else {
            print("\"val\": { ");
            print("\"id\": \"" + c.id + "\", \"type\": \"" + c.type + "\", ");
        }
        print("\"block\": { ");
        visit(c.block);
        print(" } ");
    }

/*
Utility Methods
*/
    public Environment getEnvironment() {
        return env;
    }

    protected Environment.CoolClass setType(final Environment.CoolClass cls,
            final Node node) {
        node.type = cls;
        return cls;
    }

    protected boolean moreGeneralOrEqualTo(final Environment.CoolClass c1,
            Environment.CoolClass c2) throws Environment.EnvironmentException {
        while (c2 != c1 && c2 != ANY) {
            c2 = c2.parent;
        }
        return c2 == c1;
    }

    protected Environment.CoolClass mostSpecificParent(
            Environment.CoolClass c1, Environment.CoolClass c2)
            throws Environment.EnvironmentException {
        final HashSet<Environment.CoolClass> alreadySeen = 
                new HashSet<Environment.CoolClass>();

        while (true) {
            if (alreadySeen.contains(c1) && c1 != ANY) {
                return c1;
            }
            alreadySeen.add(c1);
            c1 = c1.parent;
            if (alreadySeen.contains(c2) && c2 != ANY) {
                return c2;
            }
            alreadySeen.add(c2);
            c2 = c2.parent;
            if (c1 == c2) {
                return c1;
            }
        }
    }

    protected void print(String val) {
        if (debug) {
            System.out.print(val);
        }
    }

    protected void print(int val) {
        if (debug) {
            System.out.print(val);
        }
    }

    protected void log(final String msg) {
        if (debug) {
            System.err.println(msg);
        }
    }
}
