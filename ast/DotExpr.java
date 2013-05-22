
package ast;
import ast.typecheck.*;
public class DotExpr extends Expr
{
    public final Expr expr;
    public final String id;
	public final Actuals actuals;
	
	public DotExpr(Expr e, String i, Actuals a)
	{
		super();
        expr = e;
        id = i;
        actuals = a;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
