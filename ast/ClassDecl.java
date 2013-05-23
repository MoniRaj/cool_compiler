package ast;
import typecheck.*;
import java.util.ArrayList;
public class ClassDecl extends Node
{
    public final String type;
    public final ClassVarFormals varformals;
    public final Extension extension;
    public final ClassBody classbody;

    public ClassDecl(String t, ClassVarFormals vf, Extension e, ClassBody b) {
        super();
        type = t;
        varformals = vf;
        extension = e;
        classbody = b;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
