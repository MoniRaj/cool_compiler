package ast;
import typecheck.*;
import beaver.Symbol;
import java.util.ArrayList;
public class ExprOrNative extends Symbol
{
    public final Expr expr;
    public final boolean isnative;

    public ExprOrNative(Expr e) {
        super();
        expr = e;
        isnative = false;
    }

    public ExprOrNative() {
        super();
        expr = null;
        isnative = true;
    }
}
