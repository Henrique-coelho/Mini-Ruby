// Importante, refazer depois

package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Vector;

public class AccessExpr extends SetExpr{
    private Expr base;
    private final Expr index;
    
    public AccessExpr(int line, Expr base, Expr index){
        super(line);
        this.base = base;
        this.index = index;
    }

    @Override
    public Value<?> expr() {
        Value<?> baseValue = base.expr();
        Value<?> indexValue = index.expr();
        
        if(!(baseValue instanceof ArrayValue || indexValue instanceof IntegerValue))
            Utils.abort(super.getLine());
        else {
            ArrayValue baseValue_expanded = (ArrayValue) baseValue;
            Vector<Value<?>> array = baseValue_expanded.value();
            
            IntegerValue indexValue_expanded = (IntegerValue) indexValue;
            int i = indexValue_expanded.value();
            
            Value<?> value = null;
            if(i<0)
                i += array.size();
            if(i<0 && i<array.size())
                value = array.elementAt(i);
            if(value == null)
                value = new StringValue("");
            
            return value;
        }
        Utils.abort(super.getLine());
        return null;
    }

    @Override
    public void SetValue(Value<?> value) {
        if(index == null){
            if(base instanceof SetExpr){
                SetExpr base_expanded = (SetExpr) base;
                base_expanded.SetValue(value);
            }
        }
        else{
            Value<?> baseValue = base.expr();
            if (baseValue instanceof ArrayValue){
                ArrayValue baseValue_expanded = (ArrayValue) baseValue;
                Vector<Value<?>> array = baseValue_expanded.value();
                
                Value<?> indexValue = index.expr();
                if(indexValue instanceof IntegerValue){
                    IntegerValue indexValue_expanded = (IntegerValue) indexValue;
                    int i = indexValue_expanded.value();
                    
                    if(i < 0)
                        i += array.size(); // Número negativos são usado para acessar um array, até certo ponto
                    if(i >= 0){
                        if(i>=array.size()){
                            array.setSize(i+1);
                            for(int j=0;j<i;j++)
                                if(array.get(j)==null)
                                    array.setElementAt(new StringValue(""), i); // Array Nule gerados pela inserção são tranformados em strings vazias
                        }
                        array.setElementAt(value, i);
                    }
                    base = new ConstExpr(super.getLine(), new ArrayValue(array));
                }
                else{
                    Utils.abort(super.getLine());
                }
            }
            else {
                Utils.abort(super.getLine());
            }
        }
    }
}
