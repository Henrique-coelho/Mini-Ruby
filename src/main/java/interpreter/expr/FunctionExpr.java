
package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Vector;


public class FunctionExpr extends Expr {
    private Expr expr;
    private FunctionOp op;
    
    public FunctionExpr(int line, Expr expr, FunctionOp op) {
        super(line);
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        Value<?> value = expr.expr();
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
            
            boolean canParse = true;
            for(int i=0;i<string.length();i++){
                char c = string.charAt(i);
                
                System.out.println("Este char é: " + c);
                
                if(!(c>='0' && c<='9')){
                    canParse = false;
                    break;
                }
            }
            
            int parse = 0;
            if(canParse)
                parse = Integer.parseInt(string);
            
            return(new IntegerValue(parse));
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
