
package interpreter.expr;

import interpreter.value.ArrayValue;
import interpreter.value.Value;
import java.util.List;
import java.util.Vector;

public class ArrayExpr extends Expr{
    private List<Expr> exprs;
    
    public ArrayExpr(int line, List<Expr> exprs){
        super(line);
        this.exprs = exprs;
    }
    
    @Override
    public Value<?> expr() {
        Vector<Value<?>> array = new Vector(exprs.size());
        for(Expr expr : exprs){
            array.add(expr.expr());
        }
        return (new ArrayValue(array));
    }
    
}
