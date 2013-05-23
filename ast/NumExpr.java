
package ast;
import typecheck.*;
public class NumExpr extends Expr
{
	public final int value;
    static final int expr_type = 15;
    
	
	public NumExpr(Number value)
	{
		super(expr_type);
		this.value = value.intValue();
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
