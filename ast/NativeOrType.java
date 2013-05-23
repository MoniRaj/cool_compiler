package ast;
import typecheck.*;
import beaver.Symbol;
import java.util.ArrayList;
public class NativeOrType extends Symbol
{
    public final String type;
    public final Expr expr;
    public final boolean isnative;

    public NativeOrType(String t, Expr e) {
        super();
        expr = e;
        type = t;
        isnative = false;
    }

    public NativeOrType() {
        super();
        expr = null;
        type = "";
        isnative = true;
    }
}
