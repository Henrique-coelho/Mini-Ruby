
package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Vector;

public class BinaryExpr extends Expr{
    private Expr left;
    private BinaryOp op;
    private Expr right;
    
    public BinaryExpr(int line, Expr left, BinaryOp op, Expr right){
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public Value<?> expr() {
        Value<?> leftValue = left.expr();
        Value<?> rightValue = right.expr();
        
        if(op == BinaryOp.RangeWithOp){
            if(!(leftValue instanceof IntegerValue && rightValue instanceof IntegerValue))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                if (intLeft>intRight){ // Garante que intRight seja o maior termo
                    int aux = intLeft;
                    intLeft = intRight;
                    intRight = intLeft;
                }
                
                Vector<Value<?>> array = new Vector(intRight - intLeft + 1);
                for(int i=intLeft;i<=intRight;i++)
                    array.add(new IntegerValue(i));
                
                return (new ArrayValue(array));
            }
        }
        else if(op == BinaryOp.RangeWithoutOp){
            if(!(leftValue instanceof IntegerValue && rightValue instanceof IntegerValue))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                if (intLeft>intRight){ // Garante que intRight seja o maior termo
                    int aux = intLeft;
                    intLeft = intRight;
                    intRight = intLeft;
                }
                
                Vector<Value<?>> array = new Vector(intRight - intLeft);
                for(int i=intLeft;i<intRight;i++)
                    array.add(new IntegerValue(i));
                
                return (new ArrayValue(array));
            }
        }
        else if(op == BinaryOp.AddOp){
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)||(leftValue instanceof StringValue && rightValue instanceof StringValue)||(leftValue instanceof ArrayValue)))
                Utils.abort(super.getLine());
            else if(leftValue instanceof IntegerValue){
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue(intLeft+intRight));
            }
            else if(leftValue instanceof StringValue){
                StringValue leftValue_expanded = (StringValue) leftValue;
                StringValue rightValue_expanded = (StringValue) rightValue;
                String stringLeft = leftValue_expanded.value();
                String stringRight = rightValue_expanded.value();
                
                return (new StringValue(stringLeft.concat(stringRight)));
            }
            else {
                ArrayValue leftValue_expanded = (ArrayValue) leftValue;
                Vector<Value<?>> array = leftValue_expanded.value();
                
                // Concatenação de um inteiro a um array
                if(rightValue instanceof IntegerValue){
                    IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                    int intRight = rightValue_expanded.value();
                    
                    array.addElement(new IntegerValue(intRight));
                }
                // Concatenação de uma String a um array
                else if(rightValue instanceof StringValue){
                    StringValue rightValue_expanded = (StringValue) rightValue;
                    String stringRight = rightValue_expanded.value();
                    
                    array.addElement(new StringValue(stringRight));
                }
                // Concatenação de dois arrays
                else{
                    ArrayValue rightValue_expanded = (ArrayValue) rightValue;
                    Vector<Value<?>> rightArray = rightValue_expanded.value();
                    
                    for(Value<?> values : rightArray)
                        array.addElement(values);
                }
                return (new ArrayValue(array));
            }
        }
        else if(op == BinaryOp.SubOp){
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue(intLeft-intRight));
            }
        }
        else if(op == BinaryOp.MulOp){
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue(intLeft*intRight));
            }
        }
        else if(op == BinaryOp.DivOp){
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue(intLeft/intRight));
            }
        }
        else if(op == BinaryOp.ModOp){
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue(intLeft%intRight));
            }
        }
        else {
            if(!((leftValue instanceof IntegerValue && rightValue instanceof IntegerValue)))
                Utils.abort(super.getLine());
            else {
                IntegerValue leftValue_expanded = (IntegerValue) leftValue;
                IntegerValue rightValue_expanded = (IntegerValue) rightValue;
                int intLeft = leftValue_expanded.value();
                int intRight = rightValue_expanded.value();
                
                return (new IntegerValue((int) Math.pow(intLeft, intRight)));
            }
        }
        return null;
    }
}
