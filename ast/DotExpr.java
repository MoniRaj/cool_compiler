
package ast;
import typecheck.*;
public class DotExpr extends Expr
{
    public final Expr expr;
    public final String id;
	public final Actuals actuals;
    static final int expr_type = 4;
	
	public DotExpr(Expr e, String i, Actuals a)
	{
		super(expr_type);
        expr = e;
        id = i;
        actuals = a;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
