package ast;
import java.util.ArrayList;
public class Program extends Node
{
    public ArrayList classlist;
    public ArrayList<CoolClass> class_hierarchy;

    public Program(ArrayList clist) {
        super();
        classlist = clist;
        class_hierarchy = new ArrayList<CoolClass>();
    }

    public void add(ClassDecl c) {
        classlist.add(c);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
