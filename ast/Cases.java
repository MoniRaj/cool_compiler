package ast;
import typecheck.*;
import java.util.ArrayList;
public class Cases extends Node
{
    public ArrayList caseslist;

    public Cases(ArrayList clist) {
        super();
        caseslist = clist;
    }


    public void add(Case c) {
        caseslist.add(c);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
