package lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() throws LexicalException, IOException {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 12 && state != 13) {
            int c = getc();
            
            switch (state) {
                case 1:
                    // TODO: Implement me!
                    break;
                case 2:
                    // TODO: Implement me!
                    break;
                case 3:
                    // TODO: Implement me!
                    break;
                case 4:
                    // TODO: Implement me!
                    break;
                case 5:
                    // TODO: Implement me!
                    break;
                case 6:
                    // TODO: Implement me!
                    break;
                case 7:
                    // TODO: Implement me!
                    break;
                case 8:
                    // TODO: Implement me!
                    break;
                case 9:
                    // TODO: Implement me!
                    break;
                case 10:
                    // TODO: Implement me!
                    break;
                case 11:
                    // TODO: Implement me!
                    break;
                default:
                    throw new LexicalException("Unreachable");
            }
        }

        if (state == 12)
            lex.type = st.find(lex.token);

        return lex;
    }

    private int getc() throws LexicalException {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) throws LexicalException {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }
}
