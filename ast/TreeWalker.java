
package ast;
public class TreeWalker {
        
    //list of classes
    //TODO needs to have Environment obj

    protected Environment env;
    protected boolean debug;

    protected final Environment.CoolClass ANY;
    protected final Environment.CoolClass UNIT;
    protected final Environment.CoolClass ARRAYANY;
    protected final Environment.CoolClass SYMBOL;
    protected final Environment.CoolClass BOOLEAN;
    protected final Environment.CoolClass INT;
    protected final Environment.CoolClass STRING;
    protected final Environment.CoolClass IO;

    public static class TypeCheckException extends Exception {

        public TypeCheckException(final String msg) {
            super(msg);
        }
    }
    
    public TreeWalker(final Node root, final boolean debug)
            throws Environment.EnvironmentException {
        this.root = root;
        this.debug = debug;
        env = new Environment(debug);
        ANY = env.getClass("Any");
        ARRAYANY = env.getClass("ArrayAny");
        BOOLEAN = env.getClass("Boolean");
        UNIT = env.getClass("Unit");
        SYMBOL = env.getClass("Symbol");
        INT = env.getClass("Int");
        STRING = env.getClass("String");
        IO = env.getClass("IO");
    }

    public Environment getEnvironment() {
        return env;
    }

    public int depth = 0;

    public void print(String val) {
        System.out.print(val);
    }

    public void print(int val) {
        System.out.print(val);
    }


    public void log(final String msg) {
        if (debug) {
            System.err.println(msg);
        }
    }

/*
Visit Methods below
*/
    public void visit(Program p) {
        print("{ ");
        print("\"Program\": { ");
        //TODO 1. build class hierarchy
        //  for classdecl in program:
        //      final Environment.CoolClass new_class = new E.CC(classdecl.type)
        //      new_class.node = classdecl //TODO check that node works correctly
        //      env.addClass(new_class);
        
        //TODO 2. check parents of classes
            //  for classdecl in program:
              for (int i = 0; i < p.classlist.size(); i++) 
              {
            //      classdecl can't extend basic classes (check which ones it can't extend)
                    try{
                        if ( p.classlist.get(i).extension.type != "") 
                        {
                            final Environment.CoolClass thisClass = env.getClass((String) p.classlist.get(i).extension.type);
                            if (p.classlist.get(i).extension.type.equals("Int") || p.classlist.get(i).extension.type.equals("Bool") || p.classlist.get(i).extension.type.equals("String")) 
                            {
                                throw new TypeCheckException(MessageFormat.format("Class {0} inherits from prohibited class {1}",thisClass, p.classlist.get(i).extension.type));
                            }
                            final Environment.CoolClass parentClass = env.getClass((String) p.classlist.get(i).extension.type);
                            thisClass.parent = parentClass;
                            log(MessageFormat.format("Class {0} inherits from {1}", thisClass, parentClass));
                        } 
                        else 
                        {
                            final Environment.CoolClass thisClass = env.getClass((String) p.classlist.get(i).extension.type);
                            final Environment.CoolClass parentClass = ANY;
                            thisClass.parent = parentClass;
                            log(MessageFormat.format("Class {0} has no listed parent, so assuming it inherits from {1}", thisClass, parentClass));
                        }   
                    }
                    catch(TypeCheckException e )
                    { 
                        log(MessageFormat.format("Malformed AST; while checking classes, expected CLASS or SEMI, found {0}",Util.idToName(node.kind)));
                    }
                }
            //      classdecl's parent must exist
            //      cur_class = env.getClass(classdecl.type)
            //      set cur_class parent = to correct parent

        for (int i = 0; i < p.classlist.size(); i++) {
            print("\"class\": { ");
            visit((ClassDecl) p.classlist.get(i));
            print(" }");
            if (i < p.classlist.size()-1) print(", ");
            else print(" ");
        }
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
        print("\""+cv.id+":"+cv.type+"\"");
    }

    public void visit(Extension e) {
        if (e.isactuals) {
            print("\"type\": \""+e.type+"\",\"actuals\": { ");
            visit(e.actuals);
            print(" } ");
        }
        else {
            print("\"native\"");
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
Helper Methods below
*/

    protected Environment.CoolClass setType(final Environment.CoolClass cls,
            final Node node) {
        node.type = cls;
        return cls;
    }

}
