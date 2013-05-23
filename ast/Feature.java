package ast;
import typecheck.*;
public class Feature extends Node
{
    public final String feattype;
    public final MethodFeature methodfeature;
    public final VarFeature varfeature;
    public final BlockFeature blockfeature;

    public Feature(MethodFeature mf) {
        feattype = "method";
        methodfeature = mf;
        varfeature = null;
        blockfeature = null;
    }
 
    public Feature(VarFeature vf) {
        feattype = "var";
        methodfeature = null;
        varfeature = vf;
        blockfeature = null;
    } 

    public Feature(BlockFeature bf) {
        feattype = "block";
        methodfeature = null;
        varfeature = null;
        blockfeature = bf;
    }

    public void accept(TreeWalker walker)
    {
        walker.visit(this);
    }

}
