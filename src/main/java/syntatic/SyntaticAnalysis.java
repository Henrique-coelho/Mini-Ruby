package syntatic;

import interpreter.command.AssignCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.ForCommand;
import interpreter.command.IfCommand;
import interpreter.command.OutputCommand;
import interpreter.command.OutputOp;
import interpreter.command.UnlessCommand;
import interpreter.command.UntilCommand;
import interpreter.command.WhileCommand;
import interpreter.expr.AccessExpr;
import interpreter.expr.ArrayExpr;
import interpreter.expr.BinaryExpr;
import interpreter.expr.BinaryOp;
import interpreter.expr.BoolExpr;
import interpreter.expr.BoolOp;
import interpreter.expr.CompositeBoolExpr;
import interpreter.expr.ConstExpr;
import interpreter.expr.ConvExpr;
import interpreter.expr.ConvOp;
import interpreter.expr.Expr;
import interpreter.expr.FunctionExpr;
import interpreter.expr.FunctionOp;
import interpreter.expr.InputExpr;
import interpreter.expr.InputOp;
import interpreter.expr.NotBoolExpr;
import interpreter.expr.RelOp;
import interpreter.expr.SingleBoolExpr;
import interpreter.expr.Variable;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import java.util.Vector;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.LexicalException;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) throws LexicalException {
        this.lex = lex;
        try {
            this.current = lex.nextToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Command start() throws LexicalException {
        BlocksCommand ccmd = procCode();
        eat(TokenType.END_OF_FILE);
        return ccmd;
    }

    private void advance() throws LexicalException {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        //     current.type + ")");
        try {
            current = lex.nextToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eat(TokenType type) throws LexicalException {
        // System.out.println("Expected (..., " + type + "), found (\"" + 
        //     current.token + "\", " + current.type + ")");
        if (type == current.type) {
            try {
                current = lex.nextToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }
        System.exit(1);
    }

    // <code>     ::= { <cmd> }
    private BlocksCommand procCode() throws LexicalException {
        System.out.println("Doing procCode");
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<>();

        while (current.type == TokenType.IF ||
                current.type == TokenType.UNLESS ||
                current.type == TokenType.WHILE ||
                current.type == TokenType.UNTIL ||
                current.type == TokenType.FOR ||
                current.type == TokenType.PUTS ||
                current.type == TokenType.PRINT ||
                current.type == TokenType.ID ||
                current.type == TokenType.OPEN_PAR) {
            Command cmd = procCmd();
            cmds.add(cmd);
        }

        BlocksCommand cmd = new BlocksCommand(line, cmds);
        return cmd;
    }

    // <cmd>      ::= <if> | <unless> | <while> | <until> | <for> | <output> | <assign>
    private Command procCmd() throws LexicalException {
        System.out.println("Doing procCmd");
        Command cmd = null;
        if(current.type == TokenType.IF){
            cmd = procIf();
        }
        else if(current.type == TokenType.UNLESS){
            cmd = procUnless();
        }
        else if (current.type == TokenType.WHILE){
            cmd = procWhile();
        }
        else if (current.type == TokenType.UNTIL){
            cmd = procUntil();
        }
        else if (current.type == TokenType.FOR){
            cmd = procFor();
        }
        else if (current.type == TokenType.PUTS || current.type == TokenType.PRINT) {
            cmd = procOutput();
        } 
        else {
            cmd = procAssign();
        }
        return cmd;
    }

    // Feito
    // <if>       ::= if <boolexpr> [ then ] <code> { elsif <boolexpr> [ then ] <code> } [ else <code> ] end
    private Command procIf() throws LexicalException {
        System.out.println("Doing procIf");
        int line = lex.getLine();
        eat(TokenType.IF);
        
        BoolExpr expr = procBoolExpr();
        
        if (current.type == TokenType.THEN)
            advance();

        Command cmd = procCode();
        
        // A sequência de condições e comandos dos else if seguintes serão armazenadas em dois distintos ArrayLists
        Vector<Integer> followingLines = new Vector(0);
        Vector<BoolExpr> followingElsesExprs = new Vector(0);
        Vector<Command> followingElsesCmds = new Vector(0);

        while(current.type == TokenType.ELSIF){
            advance();
            
            followingLines.addElement(lex.getLine());
            followingElsesExprs.addElement(procBoolExpr());
            
            if (current.type == TokenType.THEN)
                advance();
            
            followingElsesCmds.addElement(procCode());
        }
        
        Command elseCmd = null; // o comando do else é nulo se não houver "else"
        if (current.type == TokenType.ELSE){
            advance();
            elseCmd = procCode();
        }
        
        eat(TokenType.END);
                
        int lastIndex = followingElsesCmds.size() - 1;
        
        for(int i=lastIndex;i>=0;i--){
            int itLine = followingLines.get(i);
            Command itCmd = followingElsesCmds.get(i);
            BoolExpr itExpr = followingElsesExprs.get(i);
            
            followingLines.remove(i);
            followingElsesCmds.remove(i);
            followingElsesExprs.remove(i);
            
            elseCmd = new IfCommand(itLine,itExpr,itCmd,elseCmd);
        }
        
        Command ifCmd = new IfCommand(line,expr,cmd,elseCmd);
        return ifCmd;
    }

    // Feito
    // <unless>   ::= unless <boolexpr> [ then ] <code> [ else <code> ] end
    private Command procUnless() throws LexicalException {
        System.out.println("Doing procUnless");
        int line = lex.getLine();
        eat(TokenType.UNLESS);
        
        BoolExpr expr = procBoolExpr();
        
        if (current.type == TokenType.THEN)
            advance();
        Command cmd = procCode();

        Command elseCmd = null;
        if(current.type == TokenType.ELSE){
            advance();
            elseCmd = procCode();
        }
        
        eat(TokenType.END);
        
        Command unlessCommand = new UnlessCommand(line,expr,cmd,elseCmd);
        return unlessCommand;
    }

    // Feito
    // <while>    ::= while <boolexpr> [ do ] <code> end
    private Command procWhile() throws LexicalException {
        System.out.println("Doing procWhile");
        int line = lex.getLine();
        eat(TokenType.WHILE);

        BoolExpr expr = procBoolExpr();

        if (current.type == TokenType.DO){
            advance();
        }

        Command cmd = procCode();

        eat(TokenType.END);
        
        Command whileCmd = new WhileCommand(line,expr,cmd);
        return whileCmd;
    }

    // Feito
    // <until>    ::= until <boolexpr> [ do ] <code> end
    private Command procUntil() throws LexicalException {
        System.out.println("Doing procUntil");
        int line = lex.getLine();
        eat(TokenType.UNTIL);
        
        BoolExpr expr = procBoolExpr();
        
        if(current.type == TokenType.DO){
            advance();
        }
        
        Command cmd = procCode();
        
        eat(TokenType.END);
        
        Command untilCmd = new UntilCommand(line,expr,cmd);
        return untilCmd;
    }

    // Feito
    // <for>      ::= for <id> in <expr> [ do ] <code> end
    private ForCommand procFor() throws LexicalException {
        System.out.println("Doing procFor");
        int line = lex.getLine();
        eat(TokenType.FOR);
        
        Variable var = null;
        if(current.type == TokenType.ID){
            var = procId();
        }
        eat(TokenType.IN);
        
        Expr expr = procExpr();
        
        BlocksCommand cmmds = null;
        if(current.type == TokenType.DO){
            advance();
            cmmds = procCode();
        }
        eat(TokenType.END);
        
        return new ForCommand(line, var, expr, cmmds);
    }

    // <output>   ::= ( puts | print ) [ <expr> ] [ <post> ] ';'
    private Command procOutput() throws LexicalException {
        System.out.println("Doing procOutput");
        OutputOp op = null;
        if (current.type == TokenType.PUTS) {
            op = OutputOp.PutsOp;
            advance();
        } 
        else if (current.type == TokenType.PRINT) {
            op = OutputOp.PrintOp;
            advance();
        } 
        else {
            showError();
        }
        
        int line = lex.getLine();

        Expr expr = null;
        if (current.type == TokenType.ADD ||
                current.type == TokenType.SUB ||
                current.type == TokenType.INTEGER ||
                current.type == TokenType.STRING ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.GETS ||
                current.type == TokenType.RAND ||
                current.type == TokenType.ID ||
                current.type == TokenType.OPEN_PAR) {
            expr = procExpr();
        }
        Command ocmd = new OutputCommand(line, op, expr); 
        
        if (current.type == TokenType.IF || current.type == TokenType.UNLESS) {
            ocmd = procPost(ocmd);
        }

        eat(TokenType.SEMI_COLON);

        return ocmd;
    }

    //Feito
    // <assign>   ::= <access> { ',' <access> } '=' <expr> { ',' <expr> } [ <post> ] ';'
    private Command procAssign() throws LexicalException {
        System.out.println("Doing procAssign");
        int line = lex.getLine();
        Vector<Expr> left = new Vector(0);
        Vector<Expr> right = new Vector(0);
        left.addElement(procAccess());
    
        while (current.type == TokenType.COMMA) {
            advance();
            left.addElement(procAccess());
        }

        eat(TokenType.ASSIGN);
        
        right.addElement(procExpr());
        while (current.type == TokenType.COMMA) {
            advance();
            right.addElement(procExpr());
        }

        Command mainCmmd = new AssignCommand(line,left,right);
        if (current.type == TokenType.IF || current.type == TokenType.UNLESS) {
            advance();
            mainCmmd = procPost(mainCmmd);
        }

        eat(TokenType.SEMI_COLON);
        return mainCmmd;
    }

    // Feito
    // <post>     ::= ( if | unless ) <boolexpr>
    private Command procPost(Command cmmd) throws LexicalException {
        System.out.println("Doing procPost");
        int line = lex.getLine();
        boolean isIf = true;
        
        if (current.type == TokenType.IF) {
            isIf = true;
            advance();
        } else if (current.type == TokenType.UNLESS) {
            isIf = false;
            advance();
        } else {
            showError();
        }

        Command mainCmmd;
        BoolExpr cond = procBoolExpr(); 
        
        if(isIf)
            mainCmmd = new IfCommand(line, cond, cmmd, null);
        else
            mainCmmd = new UnlessCommand(line, cond, cmmd, null);
        
        return mainCmmd;
    }

    // Feito
    // <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
    private BoolExpr procBoolExpr() throws LexicalException {
        System.out.println("Doing procBoolExpr");
        boolean isInverted = false;
        if( current.type == TokenType.NOT){
            isInverted = true;
            advance();
        }
        BoolExpr expr = procCmpExpr();
        int line = lex.getLine();
        
        if(current.type == TokenType.AND || current.type == TokenType.OR){
            
            BoolOp op;
            if(current.type == TokenType.AND)
                op = BoolOp.And;
            else
                op = BoolOp.Or;
            advance();
            
            BoolExpr compExpr = procBoolExpr();
            
            expr = new CompositeBoolExpr(line,expr,op,compExpr);
        }
        
        if(isInverted)
            expr = new NotBoolExpr(line,expr);
        return expr;
    }

    // Feito
    // <cmpexpr>  ::= <expr> ( '==' | '!=' | '<' | '<=' | '>' | '>=' | '===' ) <expr>
    private BoolExpr procCmpExpr() throws LexicalException {
        System.out.println("Doing procCmpExpr");
        Expr left = procExpr();
        RelOp op = null;
        if((current.type == TokenType.EQUALS) || 
                (current.type == TokenType.NOT_EQUALS) || 
                (current.type == TokenType.LOWER) ||
                (current.type == TokenType.GREATER) ||
                (current.type == TokenType.LOWER_EQ) ||
                (current.type == TokenType.GREATER_EQ) ||
                (current.type == TokenType.CONTAINS)){
            
            if(current.type == TokenType.EQUALS)
                op = RelOp.EqualsOp;
            else if(current.type == TokenType.NOT_EQUALS)
                op = RelOp.NotEqualsOp;
            else if(current.type == TokenType.LOWER)
                op = RelOp.LowerThanOp;
            else if(current.type == TokenType.LOWER_EQ)
                op = RelOp.LowerEqualOp;
            else if(current.type == TokenType.GREATER)
                op = RelOp.GreaterThanOp;
            else if(current.type == TokenType.GREATER_EQ)
                op = RelOp.GreaterEqualOp;
            else if(current.type == TokenType.CONTAINS)
                op = RelOp.ContainsOp;
            advance();
        }
        Expr right = procExpr();
        
        int line = lex.getLine();
        BoolExpr expr = new SingleBoolExpr(line,left,op,right);
        return expr;
    }

    // Feito
    // <expr>     ::= <arith> [ ( '..' | '...' ) <arith> ]
    private Expr procExpr() throws LexicalException {
        System.out.println("Doing procExpr");
        int line = lex.getLine();
        Expr mainExpr = procArith();

        if (current.type == TokenType.RANGE_WITH ||
                current.type == TokenType.RANGE_WITHOUT) {
            BinaryOp op;
            if(current.type == TokenType.RANGE_WITH)
                op = BinaryOp.RangeWithOp;
            else
                op = BinaryOp.RangeWithoutOp;
            advance();
            
            Expr expr = procArith();
            mainExpr = new BinaryExpr(line,mainExpr,op,expr);
        }

        return mainExpr;
    }

    // Feito
    // <arith>    ::= <term> { ('+' | '-') <term> }
    private Expr procArith() throws LexicalException {
        System.out.println("Doing procArith");
        int line = lex.getLine();
        Expr mainExpr = procTerm();
        
        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            BinaryOp op;
            if(current.type == TokenType.ADD)
                op = BinaryOp.AddOp;
            else
                op = BinaryOp.SubOp;
            advance();
            
            Expr expr = procTerm();
            mainExpr = new BinaryExpr(line,mainExpr,op,expr);
        }
        
        return mainExpr;
    }

    // Feito
    // <term>     ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() throws LexicalException {
        System.out.println("Doing procTerm");
        int line = lex.getLine();
        Expr mainExpr = procPower();
        
        while (current.type == TokenType.MUL ||
                current.type == TokenType.DIV ||
                current.type == TokenType.MOD) {
            BinaryOp op;
            if(current.type == TokenType.MUL)
                op = BinaryOp.MulOp;
            else if(current.type == TokenType.DIV)
                op = BinaryOp.DivOp;
            else 
                op = BinaryOp.ModOp;
            advance();
            
            Expr expr = procPower();
            mainExpr = new BinaryExpr(line,mainExpr,op,expr);
        }

        return mainExpr;
    }

    // Feito
    // <power>    ::= <factor> { '**' <factor> }
    private Expr procPower() throws LexicalException {
        System.out.println("Doing procPower");
        int line = lex.getLine();
        Expr mainExpr = procFactor();

        Vector<Expr> followingExpr = new Vector(0);
        while (current.type == TokenType.EXP) {
            advance();
            followingExpr.addElement(procFactor());
        }

        if(!followingExpr.isEmpty()){
            int secondLastIndex = followingExpr.size();
            for(int i=secondLastIndex;i>=0;i--)
                followingExpr.set(i, new BinaryExpr(line,followingExpr.get(i),BinaryOp.ExpOp,followingExpr.get(i+1)));
            return new BinaryExpr(line,mainExpr,BinaryOp.ExpOp,followingExpr.get(0));
        }
        else return mainExpr;
    }

    // Feito
    // <factor>   ::= [ '+' | '-' ] ( <const> | <input> | <access> ) [ <function> ]
    private Expr procFactor() throws LexicalException {
        System.out.println("Doing procFactor");
        int line = lex.getLine();
        
        ConvOp op = null;
        if(current.type == TokenType.ADD || current.type == TokenType.SUB){
            if(current.type == TokenType.ADD)
                op = ConvOp.PlusOp;
            else
                op = ConvOp.MinusOp;
            advance();
        }
        
        Expr expr = null;
        if( current.type == TokenType.INTEGER || current.type == TokenType.STRING || current.type == TokenType.OPEN_BRA)
            expr = procConst(); 
        else if( current.type == TokenType.GETS || current.type == TokenType.RAND )
            expr = procInput();
        else if( current.type == TokenType.ID || current.type == TokenType.OPEN_PAR )
            expr = procAccess();
        
        if (current.type == TokenType.DOT)
            expr = procFunction(expr);
        
        if(op != null)
            expr = new ConvExpr(line,op,expr);
        
        return expr;
    }

    // Feito
    // <const>    ::= <integer> | <string> | <array>
    private Expr procConst() throws LexicalException {
        System.out.println("Doing procConst");
        Expr expr;
        if (current.type == TokenType.INTEGER) {
            expr = procInteger();
        } else if (current.type == TokenType.STRING) {
            expr = procString();
        } else {
            expr = procArray();
        }
        return expr;
    }

    // Feito
    // <input>    ::= gets | rand
    private Expr procInput() throws LexicalException {
        System.out.println("Doing procInput");
        int line = lex.getLine();
        if (current.type == TokenType.GETS){
            advance();
            return (new InputExpr(line, InputOp.GetsOp));
        }
        else if (current.type == TokenType.RAND){
            advance();
            return (new InputExpr(line, InputOp.RandOp));
        }
        else
            showError();
        return null;
    }

    //Feito
    // <array>    ::= '[' [ <expr> { ',' <expr> } ] ']'
    private Expr procArray() throws LexicalException {
        System.out.println("Doing procArray");
        int line = lex.getLine();
        eat(TokenType.OPEN_BRA);

        Vector<Expr> array = new Vector(0);
        if (current.type == TokenType.ADD ||
                current.type == TokenType.SUB ||
                current.type == TokenType.INTEGER ||
                current.type == TokenType.STRING ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.GETS ||
                current.type == TokenType.RAND ||
                current.type == TokenType.ID ||
                current.type == TokenType.OPEN_PAR) {
            
            array.addElement(procExpr());

            while (current.type == TokenType.COMMA) {
                advance();
                array.addElement(procExpr());
            }
        }
        eat(TokenType.CLOSE_BRA);
        
        return(new ArrayExpr(line,array));
    }

    // Feito
    // <access>   ::= ( <id> | '(' <expr> ')' ) [ '[' <expr> ']' ]
    private Expr procAccess() throws LexicalException {
        System.out.println("Doing procAccess");
        int line = lex.getLine();
        Expr baseExpr = null;

        if (current.type == TokenType.ID){
            baseExpr = procId();
        }
        else if( current.type == TokenType.OPEN_BRA ){
            advance();
            baseExpr = procExpr();
            eat(TokenType.CLOSE_BRA);
        }
        else 
            showError();

        Expr indexExpr = null;
        if( current.type == TokenType.OPEN_BRA ){
            advance();
            indexExpr = procExpr();
            eat(TokenType.CLOSE_BRA);
        }
        
        if(indexExpr == null)
            return baseExpr;
        else
            return new AccessExpr(line,baseExpr,indexExpr);
    }

    // Feito
    // <function> ::= '.' ( length | to_i | to_s )
    private FunctionExpr procFunction(Expr expr) throws LexicalException {
        System.out.println("Doing procFunction");
        int line = lex.getLine();
        eat(TokenType.DOT);
        
        FunctionOp op = null;
        if(current.type == TokenType.LENGTH){
            op = FunctionOp.LenghtOp;
            advance();
        }
        else if(current.type == TokenType.TO_INT){
            op = FunctionOp.ToIntOp;
            advance();
        }
        else if(current.type == TokenType.TO_STR){
            op = FunctionOp.ToStringOp;
            advance();
        }
        else{
            showError();
        }
        return new FunctionExpr(line,expr,op);
    }

    // Feito
    private ConstExpr procInteger() throws LexicalException {
        System.out.println("Doing procInteger");
        String str = current.token;
        eat(TokenType.INTEGER);
        int line = lex.getLine();

        int n;
        try {
            n = Integer.parseInt(str);
        } catch (Exception e) {
            n = 0;
        }

        IntegerValue iv = new IntegerValue(n);
        ConstExpr cexpr = new ConstExpr(line, iv);
        return cexpr;
    }

    // Feito
    private ConstExpr procString() throws LexicalException {
        System.out.println("Doing procString");
        String str = current.token;
        eat(TokenType.STRING);
        int line = lex.getLine();

        StringValue sv = new StringValue(str);
        ConstExpr cexpr = new ConstExpr(line, sv);
        return cexpr;
    }

    // Feito
    private Variable procId() throws LexicalException {
        System.out.println("Doing procId");
        int line = lex.getLine();
        String identifier = current.token;
        
        eat(TokenType.ID);
        return new Variable(line,identifier);
    }

}