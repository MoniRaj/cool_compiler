package ast;
import typecheck.*;
public abstract class BinExpr extends Expr
{
	public final Expr l;
	public final Expr r;
	

	protected BinExpr(Expr left, Expr right, int expr_type)
	{
		super(expr_type);
		l = left;
		r = right;
	}
}
