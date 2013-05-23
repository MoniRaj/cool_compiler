package ast;
import typecheck.*;
public class BlockFeature extends Node
{
    public final Block block;

    public BlockFeature(Block bl) {
        super();
        block = bl;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }
}
