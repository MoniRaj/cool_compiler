
package ast;
import ast.typecheck.*;
public class LtExpr extends BinExpr
{
	public LtExpr(Expr left, Expr right)
	{
		super(left, right);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
