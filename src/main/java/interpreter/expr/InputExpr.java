
package interpreter.expr;

import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import java.util.Random;
import java.util.Scanner;


public class InputExpr extends Expr {
    private InputOp op;
    
    public InputExpr(int line, InputOp op){
        super(line);
        this.op = op;
    }
    
    @Override
    public Value<?> expr(){
        if (op == InputOp.GetsOp){
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine(); // Atenção: eu não sei se nextLine pega o \n ou não. Precisa ser conferido
            
            StringValue inputValue = new StringValue(input);
            return inputValue;
        }
        else {
            Random random = new Random();
            int input = random.nextInt();
            
            IntegerValue inputValue = new IntegerValue(input);
            return inputValue;
        }
    }
}
