package ast;
import typecheck.*;
import java.util.ArrayList;
public class Program extends Node
{
    public ArrayList classlist;
    public ArrayList<Environment.CoolClass> class_hierarchy;

    public Program(ArrayList clist) {
        super();
        classlist = clist;
        class_hierarchy = new ArrayList<Environment.CoolClass>();
    }

    public void add(ClassDecl c) {
        classlist.add(c);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
