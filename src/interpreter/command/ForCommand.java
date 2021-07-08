
package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class ForCommand extends Command{
    private Value<?> var;
    private Expr expr;
    private Command cmmds;
    
    public ForCommand(int line, Value <?> var, Expr expr, Command cmmds){
        super(line);
        this.var = var;
        this.expr = expr;
        this.cmmds = cmmds;
    }
    
    @Override
    public void execute() {
        
    }
    
}
