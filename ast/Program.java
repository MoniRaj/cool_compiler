package ast;
import java.util.ArrayList;
public class Program extends Node
{
    public ArrayList classlist;

    public Program(ArrayList clist) {
        super();
        classlist = clist;
    }

    public void add(ClassDecl c) {
        classlist.add(c);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
