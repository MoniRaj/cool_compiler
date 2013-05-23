package ast;
import typecheck.*;
import java.util.ArrayList;
public class Block extends Node
{
    public ArrayList blockitems;

    public Block(ArrayList bi) {
        super();
        blockitems = bi;
    }

    public Block() {
        super();
        blockitems = new ArrayList();
    }

    public void add(BlockItem bi) {
        blockitems.add(bi);
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
