
package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.Value;
import java.util.Vector;

public class ForCommand extends Command{
    private Variable var;
    private Expr expr;
    private Command cmmds;
    
    public ForCommand(int line, Variable var, Expr expr, Command cmmds){
        super(line);
        this.var = var;
        this.expr = expr;
        this.cmmds = cmmds;
    }
    
    @Override
    public void execute() {
        Value<?> value = expr.expr();
        if(!(value instanceof ArrayValue)){
            Utils.abort(super.getLine());
        }
        else {
            ArrayValue value_expanded = (ArrayValue) value;
            Vector<Value<?>> array = value_expanded.value();
            
            for(Value<?> values : array)
                if(!(values instanceof IntegerValue)){
                    System.out.println("For com strings!");
                    Utils.abort(super.getLine());
                }
                else {
                    var.SetValue(values);
                    cmmds.execute();
                }
        }
    }
    
}
