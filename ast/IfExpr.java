
package ast;
import typecheck.*;
public class IfExpr extends Expr
{
    public final Expr expr1;
	public final Expr expr2;
	public final Expr expr3;
    static final int expr_type = 2;

	
	public IfExpr(Expr e1, Expr e2, Expr e3)
	{
		super(expr_type);
        expr1 = e1;
		expr2 = e2;
		expr3 = e3;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
