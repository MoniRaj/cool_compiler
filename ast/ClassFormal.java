package ast;
import typecheck.*;
import java.util.ArrayList;
public class ClassFormal extends Node
{
    public final String id;
    public final String type;

    public ClassFormal(String i, String t) {
        super();
        id = i;
        type = t;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
