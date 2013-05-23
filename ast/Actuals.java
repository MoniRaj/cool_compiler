package ast;
import typecheck.*;
import java.util.ArrayList;
public class Actuals extends Node
{
    public ArrayList exprlist;

    public Actuals(ArrayList elist) {
        super();
        exprlist = elist;
    }

    public void add(Expr expr) { 
        exprlist.add(expr);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
