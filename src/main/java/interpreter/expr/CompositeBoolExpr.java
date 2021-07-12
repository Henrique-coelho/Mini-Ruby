
package interpreter.expr;

public class CompositeBoolExpr extends BoolExpr{
    private final BoolExpr left;
    private final BoolOp op;
    private final BoolExpr right;
    
    public CompositeBoolExpr (int line, BoolExpr left, BoolOp op, BoolExpr right){
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public boolean expr(){
        if(op == BoolOp.And)
            return (left.expr() && right.expr());
        else
            return (left.expr() || right.expr());
    }
}
