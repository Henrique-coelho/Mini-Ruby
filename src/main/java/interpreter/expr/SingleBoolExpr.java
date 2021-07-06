
package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Vector;

public class SingleBoolExpr extends BoolExpr{
    private final Expr left;
    private final Expr right;
    private final RelOp op;
    
    public SingleBoolExpr(int line, Expr left, RelOp op, Expr right){
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    public boolean expr(){
        Value<?> l = left.expr();
        Value<?> r = right.expr();
        
        if(op == RelOp.EqualsOp || op == RelOp.NotEqualsOp){
            boolean valueToBeReturned = false;
            if(!(((l instanceof IntegerValue)&&(r instanceof IntegerValue))||((l instanceof StringValue)&&(l instanceof StringValue)))){
                Utils.abort(super.getLine());
            }
            else {
                if(l instanceof IntegerValue){
                    IntegerValue l_expanded = (IntegerValue) l;
                    IntegerValue r_expanded = (IntegerValue) r;
                    valueToBeReturned = (l_expanded.value().equals(r_expanded.value()));
                }
                else {
                    StringValue l_expanded = (StringValue) l;
                    StringValue r_expanded = (StringValue) r;
                    valueToBeReturned = l_expanded.value().equals(r_expanded.value());
                }
            }
            if(op == RelOp.NotEqualsOp)
                valueToBeReturned = !valueToBeReturned;
            return valueToBeReturned;
        }
        else if(op != RelOp.ContainsOp){
            if(!((l instanceof IntegerValue)&&(r instanceof IntegerValue))){
                Utils.abort(super.getLine());
            }
            else {
                IntegerValue l_expanded = (IntegerValue) l;
                IntegerValue r_expanded = (IntegerValue) r;
                int l_int = l_expanded.value();
                int r_int = r_expanded.value();
                
                if(op == RelOp.GreaterThanOp)
                    return (l_int > r_int);
                if(op == RelOp.GreaterEqualOp)
                    return (l_int >= r_int);
                if(op == RelOp.LowerThanOp)
                    return (l_int < r_int);
                if(op == RelOp.LowerEqualOp)
                    return (l_int <= r_int);
            }
        }
        else if(op == RelOp.ContainsOp) {
            if(l instanceof ArrayValue && !(r instanceof ArrayValue)){
                ArrayValue l_expanded = (ArrayValue) l;
                Vector<Value<?>> array = l_expanded.value();
                
                // O item procurado é um inteiro, logo usará operações de inteiros
                if(r instanceof IntegerValue){    
                    IntegerValue r_expanded = (IntegerValue) r;
                    int value = r_expanded.value();
                    
                    for(Value<?> valueInArray : array)
                        if(valueInArray instanceof IntegerValue){
                            IntegerValue valueInArray_expanded = (IntegerValue) valueInArray;
                            if(value == valueInArray_expanded.value())
                                return true;
                        }
                }
                // Senão, o item é uma String, e logo será tratada com operações de String
                else {
                    StringValue r_expanded = (StringValue) r;
                    String value = r_expanded.value();
                    
                    for(Value<?> valueInArray : array)
                        if(valueInArray instanceof StringValue){
                            StringValue valueInArray_expanded = (StringValue) valueInArray;
                            if(value.equals(valueInArray_expanded.value()))
                                return true;
                        }
                }
                return false;
            }
        }
        return false; // FailSafe
    }
}
