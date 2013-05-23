package ast;
import typecheck.*;
import java.util.ArrayList;
public class Extension extends Node
{
    public String type;
    public boolean isactuals;
    public Actuals actuals;

    public Extension(String t, Actuals a) {
        super();
        type = t;
        isactuals = true;
        actuals = a;
    }
    
    public Extension(String t) {
        super();
        type = t;
        isactuals = false;
        actuals = null;
    }

    public Extension() {
        type = "";
        actuals = null;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
