
package ast;
import typecheck.*;
public class LeExpr extends BinExpr
{
    static final int expr_type = 8;
    
	public LeExpr(Expr left, Expr right)
	{
		super(left, right, expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
