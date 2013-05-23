
package ast;
import typecheck.*;
public class EqualsExpr extends BinExpr
{
    static final int expr_type = 5;

	public EqualsExpr(Expr left, Expr right)
	{
		super(left, right, expr_type );
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
