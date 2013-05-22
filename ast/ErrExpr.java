
package ast;
import ast.typecheck.*;
public class ErrExpr extends Expr
{
    
	public ErrExpr()
	{
		super();
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
