package ast;
import typecheck.*;
import java.util.ArrayList;
public class MethodVarFormals extends Node
{
    public ArrayList formalvarlist;

    public MethodVarFormals(ArrayList fvlist) {
        super();
        formalvarlist = fvlist;
    }

    public MethodVarFormals() {
        super();
        formalvarlist = new ArrayList();
    }

    public void add(MethodFormal fv) {
        formalvarlist.add(fv);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
