
package ast;
import typecheck.*;
public class AssignExpr extends Expr
{
    public final String id;
	public final Expr expr;
    static final int expr_type = 1;
	
	public AssignExpr(String i, Expr e)
	{
		super(expr_type);
        id = i;
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
