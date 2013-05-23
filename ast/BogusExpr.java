
package ast;
import typecheck.*;
public class BogusExpr extends Expr
{
    public final String bogus;
    static final int expr_type = 7;


    public BogusExpr(String s) {
        super(expr_type);
        bogus = s;
    }
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
