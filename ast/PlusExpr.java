
package ast;
import typecheck.*;
public class PlusExpr extends BinExpr
{
    static final int expr_type = 16;

	public PlusExpr(Expr left, Expr right)
	{
		super(left, right, expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
