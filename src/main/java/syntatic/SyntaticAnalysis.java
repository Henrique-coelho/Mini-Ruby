package syntatic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.IfCommand;
import interpreter.command.OutputCommand;
import interpreter.command.OutputOp;
import interpreter.expr.BoolExpr;
import interpreter.expr.BoolOp;
import interpreter.expr.CompositeBoolExpr;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.NotBoolExpr;
import interpreter.expr.RelOp;
import interpreter.expr.SingleBoolExpr;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
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
        Command cmd = null;
        if(current.type == TokenType.IF){
            procIf();
        }
        else if(current.type == TokenType.UNLESS){
            procUnless();
        }
        else if (current.type == TokenType.WHILE){
            procWhile();
        }
        else if (current.type == TokenType.UNTIL){
            procUntil();
        }
        else if (current.type == TokenType.FOR){
            procFor();
        }
        else if (current.type == TokenType.PUTS || current.type == TokenType.PRINT) {
            cmd = procOutput();
        } else {
            procAssign();
        }
        return cmd;
    }

    // Feito
    // <if>       ::= if <boolexpr> [ then ] <code> { elsif <boolexpr> [ then ] <code> } [ else <code> ] end
    private Command procIf() throws LexicalException {
        Command mainCmd = null;
        
        eat(TokenType.IF);
        BoolExpr expr = procBoolExpr();
        
        boolean hasThen = false;
        if (current.type == TokenType.THEN){
            hasThen = true;
            advance();
        }
        
        // Se tiver "then", então será lido várias linhas até o próximo "end", "else" ou "elseif". Senão, será lido somente uma linha
        Command cmd;
        if(hasThen)
            cmd = procCode();
        else
            cmd = procCmd();
        
        // A sequência de condições e comandos dos else if seguintes serão armazenadas em dois distintos ArrayLists
        ArrayList<BoolExpr> followingElsesExprs = new ArrayList();
        ArrayList<Command> followingElsesCmds = new ArrayList();
        
        while(current.type == TokenType.ELSIF){
            advance();
            followingElsesExprs.add(procBoolExpr());
            
            Boolean elseifHasThen = false;
            if (current.type == TokenType.THEN){
                elseifHasThen = true;
                advance();
            }
            
            // Se tiver "then", então será lido várias linhas até o próximo "end", "else" ou "elseif". Senão, será lido somente uma linha
            Command elseifCmd;
            if(elseifHasThen)
                elseifCmd = procCode();
            else
                elseifCmd = procCmd();
            followingElsesCmds.add(elseifCmd);
        }
        
        Command elseCmd = null;
        if (current.type == TokenType.ELSE){
            advance();
            elseCmd = procCode();
        }
        
        int lastIndex = followingElsesCmds.size() - 1;
        int line = lex.getLine(); // Precisa ser dinamizado o local das linhas
        
        for(int i=lastIndex;i>=0;i--){
            Command itCmd = followingElsesCmds.get(i);
            BoolExpr itExpr = followingElsesExprs.get(i);
            
            followingElsesCmds.remove(i);
            followingElsesExprs.remove(i);
            
            elseCmd = new IfCommand(line,itExpr,itCmd,elseCmd);
        }
        
        mainCmd = new IfCommand(line,expr,cmd,elseCmd);
        eat(TokenType.END);
        return mainCmd;
    }

    // <unless>   ::= unless <boolexpr> [ then ] <code> [ else <code> ] end
    private void procUnless() throws LexicalException {
        eat(TokenType.UNLESS);
        
        procBoolExpr();
        if (current.type == TokenType.THEN){
            advance();
        }
        procCode();
        
        if(current.type == TokenType.ELSE){
            advance();
            procCode();
        }
        
        eat(TokenType.END);
    }

    // <while>    ::= while <boolexpr> [ do ] <code> end
    private void procWhile() throws LexicalException {
        eat(TokenType.WHILE);

        procBoolExpr();

        if (current.type == TokenType.DO){
            advance();
        }
        
        procCode();
        eat(TokenType.END);
    }

    // <until>    ::= until <boolexpr> [ do ] <code> end
    private void procUntil() throws LexicalException {
        eat(TokenType.UNTIL);
        
        procBoolExpr();
        if(current.type == TokenType.DO){
            advance();
        }
        procCode();
        eat(TokenType.END);
    }

    // <for>      ::= for <id> in <expr> [ do ] <code> end
    private void procFor() throws LexicalException {
        eat(TokenType.FOR);
        if(current.type == TokenType.ID){
            advance();
        }
        eat(TokenType.IN);
        procBoolExpr();
        
        if(current.type == TokenType.DO){
            advance();
            procCode();
        }
        eat(TokenType.END);
    }

    // <output>   ::= ( puts | print ) [ <expr> ] [ <post> ] ';'
    private OutputCommand procOutput() throws LexicalException {
        OutputOp op = null;
        if (current.type == TokenType.PUTS) {
            op = OutputOp.PutsOp;
            advance();
        } else if (current.type == TokenType.PRINT) {
            op = OutputOp.PrintOp;
            advance();
        } else {
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

        // if (current.type == TokenType.IF || current.type == TokenType.UNLESS) {
        //     procPost();
        // }

        eat(TokenType.SEMI_COLON);

        OutputCommand ocmd = new OutputCommand(line, op, expr);
        return ocmd;
    }

    // <assign>   ::= <access> { ',' <access> } '=' <expr> { ',' <expr> } [ <post> ] ';'
    private void procAssign() throws LexicalException {
        procAccess();
    
        while (current.type == TokenType.COMMA) {
            advance();
            procAccess();
        }

        eat(TokenType.ASSIGN);
        
        procExpr();

        while (current.type == TokenType.COMMA) {
            advance();
            procExpr();
        }

        if (current.type == TokenType.IF || current.type == TokenType.UNLESS) {
            procPost();
        }

        eat(TokenType.SEMI_COLON);
    }

    // <post>     ::= ( if | unless ) <boolexpr>
    private void procPost() throws LexicalException {
        if (current.type == TokenType.IF) {
            advance();
        } else if (current.type == TokenType.UNLESS) {
            advance();
        } else {
            showError();
        }

        procBoolExpr();        
    }

    // Feito
    // <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
    private BoolExpr procBoolExpr() throws LexicalException {
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

    // <expr>     ::= <arith> [ ( '..' | '...' ) <arith> ]
    private Expr procExpr() throws LexicalException {
        Expr expr = procArith();

        if (current.type == TokenType.RANGE_WITH ||
                current.type == TokenType.RANGE_WITHOUT) {
            advance();
            procArith();
        }

        return expr;
    }

    // <arith>    ::= <term> { ('+' | '-') <term> }
    private Expr procArith() throws LexicalException {
        Expr expr = procTerm();

        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            advance();
            procTerm();
        }

        return expr;
    }

    // <term>     ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() throws LexicalException {
        Expr expr = procPower();

        while (current.type == TokenType.MUL ||
                current.type == TokenType.DIV ||
                current.type == TokenType.MOD) {
            advance();
            procPower();
        }

        return expr;
    }

    // <power>    ::= <factor> { '**' <factor> }
    private Expr procPower() throws LexicalException {
        Expr expr = procFactor();

        while (current.type == TokenType.EXP) {
            advance();
            procFactor();
        }

        return expr;
    }

    // <factor>   ::= [ '+' | '-' ] ( <const> | <input> | <access> ) [ <function> ]
    private Expr procFactor() throws LexicalException {
        if(current.type == TokenType.ADD || current.type == TokenType.SUB){
            
        }
        return procConst();
    }

    // <const>    ::= <integer> | <string> | <array>
    private Expr procConst() throws LexicalException {
        Expr expr = null;
        if (current.type == TokenType.INTEGER) {
            expr = procInteger();
        } else if (current.type == TokenType.STRING) {
            expr = procString();
        } else {
            procArray();
        }
        return expr;
    }

    // <input>    ::= gets | rand
    private void procInput() throws LexicalException {
        
    }

    // <array>    ::= '[' [ <expr> { ',' <expr> } ] ']'
    private void procArray() throws LexicalException {
        eat(TokenType.OPEN_BRA);

        if (current.type == TokenType.ADD ||
                current.type == TokenType.SUB ||
                current.type == TokenType.INTEGER ||
                current.type == TokenType.STRING ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.GETS ||
                current.type == TokenType.RAND ||
                current.type == TokenType.ID ||
                current.type == TokenType.OPEN_PAR) {
            procExpr();

            while (current.type == TokenType.COMMA) {
                advance();
                procExpr();
            }
        }

        eat(TokenType.CLOSE_BRA);
    }

    // <access>   ::= ( <id> | '(' <expr> ')' ) [ '[' <expr> ']' ]
    private void procAccess() throws LexicalException {
    }

    // <function> ::= '.' ( length | to_i | to_s )
    private void procFunction() throws LexicalException {
        eat(TokenType.DOT);
        
    }

    private ConstExpr procInteger() throws LexicalException {
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

    private ConstExpr procString() throws LexicalException {
        String str = current.token;
        eat(TokenType.STRING);
        int line = lex.getLine();

        StringValue sv = new StringValue(str);
        ConstExpr cexpr = new ConstExpr(line, sv);
        return cexpr;
    }

    private void procId() throws LexicalException {
        String identifier = current.token;
        eat(TokenType.ID);
    }

}