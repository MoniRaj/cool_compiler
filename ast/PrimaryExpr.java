
package ast;
import typecheck.*;
public class PrimaryExpr extends Expr
{
    public  String primarytype;
    public  String id;
    public  String type;
    public  Block block;
    public  Expr expr;
    public  int integer;
    public  String string;
    public  boolean bool;
	public  Actuals actuals;
    static final int expr_type = 17;

    public void initvars() {
        id = "";
        type = "";
        string = "";
        primarytype = "";
        integer = -1;
        bool = false;
        block = null;
        expr = null;
        actuals = null;

    }

	public PrimaryExpr(String pt, String i, Actuals a)
	{
		super(expr_type);
        initvars();
        primarytype = pt;
        if (pt.equals("new")) {
            type = i;
        }
        else {
            id = i;
        }
        actuals = a;
	}
	
    public PrimaryExpr(String pt, Block bl)
	{
		super(expr_type);
        initvars();
        primarytype = pt;
        block = bl;
	}
    
    public PrimaryExpr(String pt, Expr e)
	{
		super(expr_type);
        initvars();
        primarytype = pt;
        expr = e;
    }

    public PrimaryExpr(String pt) {
        super(expr_type);
        initvars();
        primarytype = pt;
    }

    public PrimaryExpr(String pt, String val) {
        super(expr_type);
        initvars();
        primarytype = pt;
        if (pt.equals("id")) {
            id = val;
        }
        else if (pt.equals("integer")) {
            integer = Integer.parseInt(val);
        }
        else if (pt.equals("string")) {
            string = val;
        }
        else if (pt.equals("boolean")) {
            bool = Boolean.parseBoolean(val);
        }
        else {
            System.out.println("SHOULD NOT HAPPEN!");

        }
    }
   /* 
    public PrimaryExpr(String pt, boolean b) {
        super();
        initvars();
        primarytype = pt;
        bool = b;
    }
    
    public PrimaryExpr(String pt, Number i) {
        super();
        initvars();
        primarytype = pt;
        integer = i.intValue();
    }
*/
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
