
package ast;
import typecheck.*;
public class DivExpr extends BinExpr
{
    static final int expr_type = 3;

	public DivExpr(Expr left, Expr right)
	{
		super(left, right, expr_type );
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
