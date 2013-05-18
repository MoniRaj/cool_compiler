
package ast;
public class AssignExpr extends Expr
{
    public final String id;
	public final Expr expr;
	
	public AssignExpr(String i, Expr e)
	{
		super();
        id = i;
		expr = e;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
