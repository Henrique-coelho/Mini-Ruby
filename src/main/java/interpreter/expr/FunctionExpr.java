
package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Vector;


public class FunctionExpr extends Expr {
    private Value<?> value;
    private FunctionOp op;
    
    public FunctionExpr(int line, Value<?> value, FunctionOp op) {
        super(line);
        this.value = value;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        if(op == FunctionOp.LenghtOp){
            if(!(value instanceof ArrayValue)){
                Utils.abort(super.getLine());
            }
            ArrayValue value_expanded = (ArrayValue) value;
            Vector<Value<?>> array = value_expanded.value();
            
            return(new IntegerValue(array.size()));
        }
        else if(op == FunctionOp.ToIntOp){
            if(!(value instanceof StringValue)){
                Utils.abort(super.getLine());
            }
            StringValue value_expanded = (StringValue) value;
            String string = value_expanded.value();
            
            return(new IntegerValue(Integer.getInteger(string, 0)));
        }
        else {
            if(!(value instanceof IntegerValue)){
                Utils.abort(super.getLine());
            }
            IntegerValue value_expanded = (IntegerValue) value;
            int integer = value_expanded.value();
            
            return(new StringValue(String.valueOf(integer)));
        }
    }
}
