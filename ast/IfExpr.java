
package ast;
public class IfExpr extends Expr
{
    public final Expr expr1;
	public final Expr expr2;
	public final Expr expr3;
	
	public IfExpr(Expr e1, Expr e2, Expr e3)
	{
		super();
        expr1 = e1;
		expr2 = e2;
		expr3 = e3;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
