
package ast;
import ast.typecheck.*;
public class NegExpr extends Expr
{
	public final Expr expr;
	
	public NegExpr(Expr e)
	{
		super();
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
