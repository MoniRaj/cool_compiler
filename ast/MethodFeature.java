package ast;
import typecheck.*;
public class MethodFeature extends Node
{
    public final String override;
    public final String id;
    public final String type;
    public final MethodVarFormals varformals;
    public final Expr expr;
    public final boolean isnative;
    public Environment.CoolClass class_type;

    public MethodFeature(String o, String i, MethodVarFormals vf, String t, ExprOrNative eon) {
        super();
        override = o + " ";
        id = i;
        type = t;
        varformals = vf;
        isnative = eon.isnative;
        expr = eon.expr;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
