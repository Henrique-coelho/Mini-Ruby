
package interpreter.command;

import interpreter.expr.BoolExpr;

public class IfCommand extends Command{
    private final BoolExpr cond;
    private final Command thenCmds;
    private final Command elseCmds;

    public IfCommand (int line, BoolExpr cond, Command thenCmds, Command elseCmds){
        super(line);
        this.cond = cond;
        this.thenCmds = thenCmds;
        this.elseCmds = elseCmds;
    }
    
    @Override
    public void execute() {
        if(cond.expr()){
            thenCmds.execute();
        }
        else if(elseCmds != null){
            elseCmds.execute();
        }
    }
}
