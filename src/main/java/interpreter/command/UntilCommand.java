
package interpreter.command;

import interpreter.expr.BoolExpr;


public class UntilCommand extends Command{
    private BoolExpr cond;
    private Command cmds;
    
    public UntilCommand(int line, BoolExpr cond, Command cmds){
        super(line);
        this.cond = cond;
        this.cmds = cmds;
    }
    
    @Override
    public void execute() {
        while(!cond.expr()){
            cmds.execute();
        }
    }
}
