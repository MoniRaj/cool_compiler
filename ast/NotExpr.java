
package ast;
public class NotExpr extends Expr
{
	public final Expr expr;
	
	public NotExpr(Expr e)
	{
		super();
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
