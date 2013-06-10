
package ast;
import typecheck.*;
import beaver.Symbol;

public abstract class Node extends Symbol
{
    public Environment.CoolClass class_type;
	public abstract void accept(TreeWalker walker); 
}
