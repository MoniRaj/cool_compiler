package ast;
import typecheck.*;
public abstract class Expr extends Node
{
    public Environment.CoolClass class_type;
    public int expr_type;
    
    public Expr(int type)
    {
        super();
        class_type = null;
        expr_type = type;
    }
}
