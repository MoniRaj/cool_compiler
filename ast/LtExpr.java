
package ast;
import ast.typecheck.*;
public class LtExpr extends BinExpr
{
    static final int expr_type = 9;

	public LtExpr(Expr left, Expr right)
	{
		super(left, right, expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
