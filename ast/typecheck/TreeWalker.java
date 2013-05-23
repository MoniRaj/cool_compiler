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
 * Much of this work is inspired (or directly written) by  Nick Chaimov (nchaimov@uoregon.edu), Winter 2010
 *
 * Modified by: Paul Elliott and Monisha Balireddi (Spr 2013)
 */

import ast.*;
import main.Terminals;
import beaver.*;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class TreeWalker {
     
    public static class TypeCheckException extends Exception {

        public TypeCheckException(final String msg) {
            super(msg);
        }
    }

    protected Node root;
    protected Environment env;
    protected boolean debug;
    protected HashMap<int, String> expr_types;

    protected Environment.CoolClass CURR_CLASS;

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
    static public final int PRIMARYEXPR = 17 ;
    static public final int WHILEEXPR = 18;

        
    public TreeWalker(final Node root, final boolean debug)
            throws Environment.EnvironmentException {
        this.root = root;
        this.debug = debug;
        env = new Environment(debug);

        CURR_CLASS = null;

        expr_types = new HashMap<int, String>();
        //put the types here 
        expr_types.put(1, ASSIGNEXPR );
        expr_types.put(2, IFEXPR );
        expr_types.put(3, DIVEXPR );
        expr_types.put(4, DOTEXPR );
        expr_types.put(5, EQUALSEXPR );
        expr_types.put(6, ERREXPR );
        expr_types.put(7, BOGUSEXPR );
        expr_types.put(8, LEEXPR );
        expr_types.put(9, LTEXPR );
        expr_types.put(10, MATCHEXPR );
        expr_types.put(11, MINUSEXPR );
        expr_types.put(12, MULTEXPR );
        expr_types.put(13, NEGEXPR );
        expr_types.put(14, NOTEXPR );
        expr_types.put(15, NUMEXPR );
        expr_types.put(16, PLUSEXPR );
        expr_types.put(17, PRIMARYEXPR );
        expr_types.put(18, WHILEEXPR );
        
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
    }

/*
Helper Methods
*/
    
    //TODO check that these helper methods work correctly

    protected void addMethod(MethodFeature mf) 
        throws Environment.EnvironmentException, TypeCheckException {
        final Environment.CoolClass return_type = env.getClass(
                mf.type);
        final Environment.CoolMethod method = new Environment.CoolMethod(
                mf.id, return_type, mf.expr);
        method.node = mf;
        processMethodArguments(method, mf.varformals);
        env.addMethod(CURR_CLASS, method);
        //node.type = return_type; //TODO do we need this?
    }

    protected void addAttribute(String i, String t, Node n, Expr e)
        throws Environment.EnvironmentException, TypeCheckException {
        final Environment.CoolClass type = env.getClass(t);
        final Environment.CoolAttribute attr = new Environment.CoolAttribute(
                i, type, e);
        attr.node = n; //TODO check that this works
        env.addAttribute(CURR_CLASS, attr);
        //node.type = t; //TODO do we need this?
    }

    protected void processMethodArguments(final Environment.CoolMethod method,
            final MethodVarFormals methodvf) 
            throws Environment.EnvironmentException, TypeCheckException {
        for (int i = 0; i < methodvf.formalvarlist.size(); i++) {
            MethodFormal mf = (MethodFormal) methodvf.formalvarlist.get(i);
            final Environment.CoolClass type = env.getClass(mf.type);
            method.arguments.add(new Environment.CoolAttribute(mf.id, type));
        }
    }

    protected void inheritAttributes(final Environment.CoolClass c) {
        if (!c.attr_inherit_done && c != ANY) {
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
                    c.attr_list.add(a);
                }
            }
            c.attr_inherit_done = true;
        }
        log("Attribute inheritance complete for class: " + c);
        if (debug) {
            for (final Environment.CoolAttribute a : c.attr_list) {
                System.err.println(MessageFormat.format("In {0} is {1}", c, a));
            }
        }
    }

    protected void inheritMethods(final Environment.CoolClass c) {
        if (!c.method_inherit_done && c != ANY) {
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
                for (final Environment.CoolMethod a : 
                        curr_class.methods.values()) {
                    log("Found method " + a + " of " + curr_class + " for " + c);
                    final Environment.CoolMethod overriddenMethod = c.methods
                            .get(a.name);
                    log(overriddenMethod != null ? "" + overriddenMethod.owner
                            : "not overridden");
                    if (overriddenMethod != null) {
                        if (!c.method_list.contains(overriddenMethod)) {
                            c.method_list.add(overriddenMethod);
                        }
                    } else {
                        if (!c.method_list.contains(a)) {
                            c.method_list.add(a);
                        }
                    }
                }
            }
            c.method_inherit_done = true;
        }
        log("Method inheritance complete for class: " + c);
        if (debug) {
            for (final Environment.CoolAttribute a : c.attr_list) {
                System.err.println(MessageFormat.format("In {0} is {1}", c, a));
            }
        }
    }

    public void checkAttributes() throws Environment.EnvironmentException,
           TypeCheckException {
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
                if (attr.expr != null) {
                    log("Checking attribute " + attr);
                    check(curr_class, attr.expr); 
                    log(MessageFormat.format("Expr type: {0}; Attr type: {1}",
                            attr.expr.class_type, attr.type));
                    if (!moreGeneralOrEqualTo(attr.expr.class_type,
                            attr.type)) { 
                        throw new TypeCheckException(MessageFormat.format(
                                "Attribute {0} has value of wrong type: {1}",
                                attr, attr.expr.class_type)); 
                    }
                }
            }
        }
    }

    public void checkMethods() throws Environment.EnvironmentException,
            TypeCheckException {
        for (final Entry<String, Environment.CoolClass> e : 
                env.class_map.entrySet()) {
            final Environment.CoolClass curr_class = e.getValue();
            if (curr_class.builtin) {
                continue;
            }
            log(MessageFormat.format("Typechecking methods of class {0}",
                    curr_class));
            for (final Entry<String, Environment.CoolMethod> e2 : 
                    curr_class.methods.entrySet()) {
                final Environment.CoolMethod method = e2.getValue();
                if (method.expr != null) {
                    log("Checking method " + method);
                    for (final Environment.CoolAttribute a : method.arguments) {
                        log(MessageFormat.format(
                                "Pushing method arg {0} onto local environment",
                            a));
                        env.local_types.push(a.name, a.type);
                    }
                    log(MessageFormat.format("Local environment is {0}",
                            env.local_types));
                    check(curr_class, method.expr);
                    for (@SuppressWarnings("unused")
                    final Environment.CoolAttribute a : method.arguments) {
                        log("Popping local environment");
                        env.local_types.pop();
                    }
                    log(MessageFormat.format("Local environment is {0}",
                            env.local_types));
                    log(MessageFormat.format(
                            "Declared method type: {0}; Method body type: {1}",
                            method.type, method.expr.class_type));
                    if (!moreGeneralOrEqualTo(method.type,
                            method.expr.class_type)) {
                        throw new TypeCheckException(MessageFormat.format(
                                "Method {0} has body of wrong type: {1}",
                                method, method.expr.class_type));
                    }
                }
            }
        }
    }

    public Environment.CoolClass check(final Environment.CoolClass curr_class,
            final Expr expr) throws Environment.EnvironmentException,
            TypeCheckException {
        if (expr != null) {
            switch (expr.expr_type) {
                
                case PRIMARYEXPR :
                    //Literals
                    if ((PrimaryExpr) expr.primarytype.equals("boolean")) {
                        return setType(BOOLEAN, expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("integer")) {
                        return setType(INT, expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("string")) {
                        return setType(STRING, expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("id")) {
                        return setType(env.lookupAttrType(curr_class, id), expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("this")) {
                        return setType(curr_class, expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("new")) {
                        Environment.CoolClass type = env.getClass(expr.type);
                        if (type == ANY || type == INT || type == BOOLEAN ||
                                type == UNIT || type == SYMBOL) {
                            throw new TypeCheckException(
                                    "Illegal use of <new> with type: " + type);
                        }
                        return setType(env.getClass(expr.type), expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("null")) {
                        return setType(NULL, expr);
                    }
                    else if ((PrimaryExpr) expr.primarytype.equals("empty")) {
                        return setType(UNIT, expr);
                    }
                    //Block
                    //TODO FINISH
                    else if ((PrimaryExpr) expr.primarytype.equals("block")) {
                        //empty block
                        //one expr block
                        //block : first expr = expr eval
                        //block : first expr = local var (see type rules on pg 21)
                    }
                    //( expr )
                    else if ((PrimaryExpr) expr.primarytype.equals("parenexpr")) {
                        return setType(check(curr_class, (PrimaryExpr) expr.expr),
                                expr); //TODO check to make sure recursion works
                    }
                    //super.methodcall
                    else if ((PrimaryExpr) expr.primarytype.equals("supercall")) {
                        //Typecheck the exprs in actuals, if any
                        Environment.CoolClass superclass = curr_class.parent; 
                        for (int i = 0; i < expr.actuals.exprlist.size(); i++) {
                            check(curr_class, (Expr) a.exprlist.get(i));
                        }
                        log(MessageFormat.format("Looking up method {0} in {1}",
                            expr.id, superclass));
                        final Environment.CoolMethod method = env.lookupMethod(
                            superclass, expr.id);
                        if (method == null) {
                            throw new TypeCheckException(MessageFormat.format(
                                    "Tried to call method {0} in {1}, but method not found.",
                                    expr.id, superclass));
                        }
                        //Typecheck: compare formals to actuals (# and type)
                        typecheckMethodArguments(method, expr.actuals);
                        return setType(method.type, expr);
                    }
                    //this.methodcall
                    else if ((PrimaryExpr) expr.primarytype.equals("call")) {
                        //Typecheck the exprs in actuals, if any
                        for (int i = 0; i < expr.actuals.exprlist.size(); i++) {
                            check(curr_class, (Expr) a.exprlist.get(i));
                        }
                        log(MessageFormat.format("Looking up method {0} in {1}",
                            expr.id, curr_class));
                        final Environment.CoolMethod method = env.lookupMethod(
                            curr_class, expr.id);
                        if (method == null) {
                            throw new TypeCheckException(MessageFormat.format(
                                    "Tried to call method {0} in {1}, but method not found.",
                                    expr.id, curr_class));
                        }
                        //Typecheck: compare formals to actuals (# and type)
                        typecheckMethodArguments(method, expr.actuals);
                        return setType(method.type, expr);
                    }
                    break;
               
                //expr.methodcall
                case DOTEXPR :
                    //Typecheck the exprs in actuals, if any
                    for (int i = 0; i < expr.actuals.exprlist.size(); i++) {
                        check(curr_class, (Expr) a.exprlist.get(i));
                    }
                    Environment.CoolClass expr_cls = check(curr_class, expr.expr);
                    log(MessageFormat.format("Looking up method {0} in {1}",
                            expr.id, expr_cls));
                    final Environment.CoolMethod method = env.lookupMethod(
                            expr_cls, expr.id);
                    if (method == null) {
                        throw new TypeCheckException(MessageFormat.format(
                                "Tried to call method {0} in {1}, but method not found.",
                                expr.id, expr_cls));
                    }
                    //Typecheck: compare formals to actuals (# and type)
                    typecheckMethodArguments(method, expr.actuals);
                    return setType(method.type, expr);
                    break;

                //Assignments
                case ASSIGNEXPR :
                    final Environment.CoolClass id_type = env.lookupAttrType(
                            curr_class, expr.id);
                    final Environment.CoolClass expr_type = check(curr_class,
                            expr.expr);
                    log(MessageFormat.format(
                            "Assignment: {0} has type {1}; expr has type {2}",
                                    expr.id, id_type, expr_type));
                    if ((expr_type == NULL) &&
                            (id_type == BOOLEAN ||
                            id_type == INT ||
                            id_type == UNIT)) {
                        throw new TypecheckException(
                                "Cannot assign <boolean,int,unit> id to Null");
                    }
                    else if (moreGeneralOrEqualTo(id_type, expr_type)) {
                        log(MessageFormat.format(
                                "Most specific parent in common is {0}",
                                mostSpecificParent(id_type, expr_type)));
                        return setType(UNIT, expr);
                    } else {
                        throw new TypeCheckException(MessageFormat.format(
                                "Expr of type {0} not compatible with {1} of type {2}",
                                expr_type, expr.id, id_type));
                    }

                //Control statements
                //TODO FINISH
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
                //exprSeq ::= LBRACE expr:e SEMI optExprSeq:eseq RBRACE {: RESULT = new ASTnode(sym.SEMI, e, null, eseq, null); :};
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

                //Boolean operators
                case LTEXPR:
                case LEQEXPR: 
                    Environment.CoolClass l_type = check(curr_class, expr.l);
                    Environment.CoolClass r_type = check(curr_class, expr.r);
                    if (l_type != INT) {
                        throw new TypeCheckException(
                                "Left argument of comparison must be Int, found "
                                + l_type);
                    }
                    if (r_type != INT) {
                        throw new TypeCheckException(
                                "Left argument of comparison must be Int, found "
                                + r_type);
                    }
                    return setType(BOOLEAN, expr);
                    break;
                case EQUALSEXPR : 
                    Environment.CoolClass l_type = check(curr_class, expr.l);
                    Environment.CoolClass r_type = check(curr_class, expr.r);
                    //Will eventually need to catch l_type == null at runtime
                    return setType(BOOLEAN, expr);
                    break;
                case NEGEXPR :
                    Environment.CoolClass type = check(curr_class, expr.expr);
                    if (type != INT) {
                        throw new TypeCheckException(
                                "Illegal use of - operator: expected Int, found "
                                + type);
                    }
                    return setType(INT, expr);
                    break;
                case NOTEXPR :
                    Environment.CoolClass type = check(curr_class, expr.expr);
                    if (type != BOOLEAN) {
                        throw new TypeCheckException(
                                "Illegal use of ! operator: expected Bool, found "
                                + type);
                    }
                    return setType(BOOLEAN, expr);
                    break;

                //Math operators
                case MINUSEXPR :
                case PLUSEXPR :
                case TIMESEXPR :
                case DIVEXPR : 
                    Environment.CoolClass l_type = check(curr_class, expr.l);
                    Environment.CoolClass r_type = check(curr_class, expr.r);
                    if (node.left.type != INT || node.right.type != INT) {
                        throw new TypeCheckException(
                                "Invalid arithmetic: both arguments must be Int");
                    }
                    return setType(INT, node);
                    break;
                
               default :
                    System.out.println(typeToString(expr.expr_type));
                    throw new TypeCheckException("Something went really wrong.");
            }
        }
        return null;
    }

    protected void typecheckMethodArguments(final Environment.CoolMethod method,
            final Actuals a) throws Environment.EnvironmentException,
            TypeCheckException {

        final List<Environment.CoolClass> actual_args = 
            new LinkedList<Environment.CoolClass>();
        getArgumentTypes(a, actual_args);
        final List<Environment.CoolAttribute> formal_args = method.arguments;

        if (actual_args.size() != formal_args.size()) {
            throw new TypeCheckException(MessageFormat.format(
                    "Call to method {0} has wrong number of arguments (expected {1}, found {2})",
                    method, formal_args.size(), actual_args.size()));
        }

        final Iterator<Environment.CoolClass> a_it = actual_args.iterator();
        final Iterator<Environment.CoolAttribute> f_it = formal_args.iterator();

        while (a_it.hasNext() && f_it.hasNext()) {
            final Environment.CoolClass expected_type = f_it.next().type;
            final Environment.CoolClass actual_type = a_it.next();

            if (!moreGeneralOrEqualTo(expected_type, actual_type)) {
                throw new TypeCheckException(MessageFormat.format(
                        "Expected argument of type {0}, but found {1}",
                        expected_type, actual_type));
            }
        }
    }

    protected List<Environment.CoolClass> getArgumentTypes(final Actuals a,
            final List<Environment.CoolClass> list) {
        for (int i = 0; i < a.exprlist.size(); i++) {
            Environment.CoolClass type = (Expr) a.exprlist.get(i).class_type;
            list.add(type);
        }
        return list;
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
            catch(EnvironmentException e)
            {
                log(MessageFormat.format("Environment error: {0}", e.message));
            }
            catch(TypeCheckException e)
            { 
                log(MessageFormat.format("Type check error: {0}", e.message));
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
            if (curr_class == NULL || curr_class == NOTHING)
                continue; //Do nothing for null/nothing
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

        //6. Check for Main class
        //TODO FINISH

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
        addAttribute(cv.id, cv.type, cv, null);
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
            addAttribute(vf.id, vf.type, vf, vf.expr);
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
            final Expr expr) {
        expr.class_type = cls;
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

    protected void typeToString(final int type) {
        return expr_types.get(type);
    }
}
