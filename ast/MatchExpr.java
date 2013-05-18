
package ast;
public class MatchExpr extends Expr
{
    public final Expr expr;
    public final Cases cases;
	
	public MatchExpr(Expr e, Cases c)
	{
		super();
        expr = e;
        cases = c;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
