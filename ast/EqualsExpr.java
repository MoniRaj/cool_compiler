
package ast;
import ast.typecheck.*;
public class EqualsExpr extends BinExpr
{
	public EqualsExpr(Expr left, Expr right)
	{
		super(left, right);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
