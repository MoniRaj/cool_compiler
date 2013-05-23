
package ast;
import typecheck.*;
public class NotExpr extends Expr
{
	public final Expr expr;
    static final int expr_type = 14;

	
	public NotExpr(Expr e)
	{
		super(expr_type);
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
