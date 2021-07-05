package lexical;

public enum TokenType {
    // SPECIALS
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,

    // SYMBOLS
    SEMI_COLON,    // ;
    COMMA,         // ,
    ASSIGN,        // =
    DOT,           // .

    // OPERATORS
    EQUALS,        // ==
    NOT_EQUALS,    // !=
    LOWER,         // <
    GREATER,       // >
    LOWER_EQ,      // <=
    GREATER_EQ,    // >=
    CONTAINS,      // ===
    RANGE_WITH,    // ..
    RANGE_WITHOUT, // ...
    ADD,           // +
    SUB,           // -
    MUL,           // *
    DIV,           // /
    MOD,           // %
    EXP,           // **

    // KEYWORDS
    IF,            // if
    THEN,          // then
    ELSIF,         // elsif  
    ELSE,          // else
    END,           // end
    UNLESS,        // unless
    WHILE,         // while
    DO,            // do
    UNTIL,         // until
    FOR,           // for
    IN,            // in
    PUTS,          // puts
    PRINT,         // print
    NOT,           // not
    AND,           // and
    OR,            // or
    GETS,          // gets
    RAND,          // rand
    OPEN_BRA,      // [
    CLOSE_BRA,     // ]
    OPEN_PAR,      // (
    CLOSE_PAR,     // )
    LENGTH,        // length
    TO_INT,        // to_i
    TO_STR,        // to_s

    // OTHERS
    ID,            // identifier
    INTEGER,       // integer
    STRING         // string

};
