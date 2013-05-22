
package ast;
import ast.typecheck.*;
public class MultExpr extends BinExpr
{
	public MultExpr(Expr left, Expr right)
	{
		super(left, right);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
