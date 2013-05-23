
package ast;
import typecheck.*;
public class MatchExpr extends Expr
{
    public final Expr expr;
    public final Cases cases;
    static final int expr_type = 10;

	public MatchExpr(Expr e, Cases c)
	{
		super(expr_type);
        expr = e;
        cases = c;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
