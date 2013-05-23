package ast;
import typecheck.*;
import java.util.ArrayList;
import beaver.Symbol;
public class IdOrNull extends Symbol
{
    public final boolean isnull;
    public final String id;
    public final String type;

    public IdOrNull(String i, String t) {
        super();
        isnull = false;
        id = i;
        type = t;
    }

    public IdOrNull() {
        super();
        isnull = true;
        id = "";
        type = "";
    }
}
