
package ast;
import typecheck.*;
public class ErrExpr extends Expr
{
        static final int expr_type = 6;

	public ErrExpr()
	{
		super(expr_type);
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
