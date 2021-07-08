package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class OutputCommand extends Command {
  private OutputOp op;
  private Expr expr;

  public OutputCommand(int line, OutputOp op) {
    this(line, op, null);
  }
  
  public OutputCommand(int line, OutputOp op, Expr expr) {
    super(line);
    this.op = op;
    this.expr = expr;
  }

  @Override
  public void execute() {
    if(expr!=null){
        Value<?> v = expr.expr();
        System.out.println(v.toString());
    }
    if(op == OutputOp.PutsOp)
        System.out.println();
    }
}
