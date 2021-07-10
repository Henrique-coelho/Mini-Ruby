
package interpreter.expr;

import interpreter.util.Memory;
import interpreter.value.Value;

public class Variable extends SetExpr{
    private final String name;
    
    public Variable (int line, String name){
        super(line);
        this.name = name;
    }
    
    @Override
    public Value<?> expr() {
        return Memory.read(name);
    }

    @Override
    public void SetValue(Value<?> value) {
        Memory.write(name, value);
    }
    
}
