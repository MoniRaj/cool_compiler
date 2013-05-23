
package ast;
import typecheck.*;
public class MultExpr extends BinExpr
{
    static final int expr_type = 12;

	public MultExpr(Expr left, Expr right)
	{
		super(left, right, expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
