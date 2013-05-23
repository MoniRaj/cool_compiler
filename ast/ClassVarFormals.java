package ast;
import typecheck.*;
import java.util.ArrayList;
public class ClassVarFormals extends Node
{
    public ArrayList formalvarlist;

    public ClassVarFormals(ArrayList fvlist) {
        super();
        formalvarlist = fvlist;
    }

    public ClassVarFormals() {
        super();
        formalvarlist = new ArrayList();
    }

    public void add(ClassFormal fv) {
        formalvarlist.add(fv);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
