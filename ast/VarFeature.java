package ast;
import typecheck.*;
public class VarFeature extends Node 
{
    public final String id;
    public final boolean isnative;
    public final String type;
    public final Expr expr;


    public VarFeature(String i, NativeOrType nt) {
        super();
        id = i;
        isnative = nt.isnative;
        type = nt.type;
        expr = nt.expr;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
