
package interpreter.expr;

import interpreter.value.Value;


public class FunctionExpr extends Expr {
      private Value<?> value;

  public FunctionExpr(int line, Value<?> value) {
    super(line);
    this.value = value;
  }

  @Override
  public Value<?> expr() {
    return value;
  }
}
