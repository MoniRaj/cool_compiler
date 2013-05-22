
package ast;
import ast.typecheck.*;
public class LeExpr extends BinExpr
{
	public LeExpr(Expr left, Expr right)
	{
		super(left, right);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
