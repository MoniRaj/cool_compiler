package ast;
import typecheck.*;
import java.util.ArrayList;
public class BlockItem extends Node
{
    public final String id;
    public final String type;
    public final Expr expr;

    public BlockItem(String i, String t, Expr e) {
        super();
        id = i;
        type = t;
        expr = e;
    }

    public BlockItem(Expr e) {
        super();
        id = "";
        type = "";
        expr = e;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
