
package ast;
import typecheck.*;
public class NegExpr extends Expr
{
    static final int expr_type = 13;

	public final Expr expr;
	
	public NegExpr(Expr e)
	{
		super(expr_type);
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
