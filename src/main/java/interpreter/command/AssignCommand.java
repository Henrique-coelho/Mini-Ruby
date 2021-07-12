
package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import java.util.List;

public class AssignCommand extends Command{
    private List<Expr> left;
    private List<Expr> right;
    
    public AssignCommand(int line, List<Expr> left, List<Expr> right){
        super(line);
        this.left = left;
        this.right = right;
    }
    
    @Override
    public void execute() {
        if(left.size() != right.size())
            Utils.abort(super.getLine());
        else {
            int size = left.size();
            for(int i=0;i<size;i++){
                if(!(left.get(i) instanceof Variable)){
                    Utils.abort(super.getLine());
                }
                else {
                    Variable var = (Variable) left.get(i);
                    var.SetValue(right.get(i).expr());
                    left.add(i, var);
                }
            }
        }
    }
    
}
