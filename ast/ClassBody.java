package ast;
import typecheck.*;
import java.util.ArrayList;
public class ClassBody extends Node
{
    public ArrayList featlist;

    public ClassBody(ArrayList flist) {
        super();
        featlist = flist;
    }

    public ClassBody() {
        featlist = new ArrayList();
    }

    public void add(Feature f) {
        featlist.add(f);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
