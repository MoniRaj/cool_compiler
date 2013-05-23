
package ast;
import typecheck.*;
public class MinusExpr extends BinExpr
{
    static final int expr_type = 11;

	public MinusExpr(Expr left, Expr right)
	{
		super(left, right, expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
