
package ast;
import ast.typecheck.*;
public class WhileExpr extends Expr
{
    public final Expr expr1;
	public final Expr expr2;
	
	public WhileExpr(Expr e1, Expr e2)
	{
		super();
        expr1 = e1;
		expr2 = e2;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
