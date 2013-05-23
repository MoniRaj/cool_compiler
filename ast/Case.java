package ast;
import typecheck.*;
public class Case extends Node 
{
    public final Block block;
    public final boolean isnull;
    public final String id;
    public final String type;


    public Case(IdOrNull ion, Block bl) {
        super();
        isnull = ion.isnull;
        id = ion.id;
        type = ion.type;
        block = bl;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
