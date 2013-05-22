
package ast;
import ast.typecheck.*;
public class BogusExpr extends Expr
{
    public final String bogus;

    public BogusExpr(String s) {
        bogus = s;
    }
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
