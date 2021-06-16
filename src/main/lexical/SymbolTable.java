package lexical;

import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

    private Map<String, TokenType> st;

    public SymbolTable() {
        st = new HashMap<String, TokenType>();

        // SYMBOLS
        st.put(";", TokenType.SEMI_COLON);
        st.put(",", TokenType.COMMA);
        st.put("=", TokenType.ASSIGN);
        st.put(".", TokenType.DOT);

        // OPERATORS
        st.put("==", TokenType.EQUALS);
        st.put("!=", TokenType.NOT_EQUALS);
        st.put("<", TokenType.LOWER);
        st.put(">", TokenType.GREATER);
        st.put("<=", TokenType.LOWER_EQ);
        st.put(">=", TokenType.GREATER_EQ);
        st.put("===", TokenType.CONTAINS);
        st.put("..", TokenType.RANGE_WITH);
        st.put("...", TokenType.RANGE_WITHOUT);
        st.put("+", TokenType.ADD);
        st.put("-", TokenType.SUB);
        st.put("*", TokenType.MUL);
        st.put("/", TokenType.DIV);
        st.put("%", TokenType.MOD);
        st.put("**", TokenType.EXP);

        // KEYWORDS
        st.put("if", TokenType.IF);
        st.put("then", TokenType.THEN);
        st.put("elsif", TokenType.ELSIF);
        st.put("else", TokenType.ELSE);
        st.put("end", TokenType.END);
        st.put("unless", TokenType.UNLESS);
        st.put("while", TokenType.WHILE);
        st.put("do", TokenType.DO);
        st.put("until", TokenType.UNTIL);
        st.put("for", TokenType.FOR);
        st.put("in", TokenType.IN);
        st.put("puts", TokenType.PUTS);
        st.put("print", TokenType.PRINT);
        st.put("not", TokenType.NOT);
        st.put("and", TokenType.AND);
        st.put("or", TokenType.OR);
        st.put("gets", TokenType.GETS);
        st.put("rand", TokenType.RAND);
        st.put("[", TokenType.OPEN_BRA);
        st.put("]", TokenType.CLOSE_BRA);
        st.put("(", TokenType.OPEN_PAR);
        st.put(")", TokenType.CLOSE_PAR);
        st.put("length", TokenType.LENGTH);
        st.put("to_i", TokenType.TO_INT);
        st.put("to_s", TokenType.TO_STR);
    }

    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public TokenType find(String token) {
        return this.contains(token) ?
            st.get(token) : TokenType.ID;
    }
}
