import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexer for BudgetDSL.
 * Scans source code character by character and produces a list of tokens.
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    
    // Position tracking
    private int start = 0;      // Start of current token
    private int current = 0;    // Current character position
    private int line = 1;       // Current line number
    
    // Keywords map for quick lookup
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("budget", TokenType.BUDGET);
        keywords.put("income", TokenType.INCOME);
        keywords.put("expense", TokenType.EXPENSE);
        keywords.put("limit", TokenType.LIMIT);
        keywords.put("print", TokenType.PRINT);
        keywords.put("if", TokenType.IF);
        keywords.put("then", TokenType.THEN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("end", TokenType.END);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("return", TokenType.RETURN);
        keywords.put("as", TokenType.AS);
        keywords.put("in", TokenType.IN);
        keywords.put("to", TokenType.TO);
    }
    
    /**
     * Creates a new Lexer for the given source code.
     * @param source The source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
    }
    
    /**
     * Scans the entire source code and returns a list of tokens.
     * @return List of tokens, ending with an EOF token
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next token
            start = current;
            scanToken();
        }
        
        // Add EOF token at the end
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }
    
    /**
     * Scans a single token.
     */
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            // Single-character tokens
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.MULTIPLY); break;
            case '%': addToken(TokenType.MODULO); break;
            
            // Potentially two-character tokens
            case '/':
                if (match('/')) {
                    // Single-line comment: skip until end of line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    // Multi-line comment: skip until */
                    skipBlockComment();
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
                
            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGN);
                break;
                
            case '!':
                if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    throw new RuntimeException("Unexpected character '!' at line " + line);
                }
                break;
                
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
                
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            
            // Whitespace - skip but track newlines
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
                
            case '\n':
                line++;
                break;
            
            // String literals
            case '"':
                scanString();
                break;
            
            default:
                if (isDigit(c)) {
                    scanNumber();
                } else if (isAlpha(c)) {
                    scanIdentifier();
                } else {
                    throw new RuntimeException("Unexpected character '" + c + "' at line " + line);
                }
                break;
        }
    }
    
    /**
     * Scans a string literal enclosed in double quotes.
     */
    private void scanString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        
        if (isAtEnd()) {
            throw new RuntimeException("Unterminated string at line " + line);
        }
        
        // Consume closing "
        advance();
        
        // Extract string value (without quotes)
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
    
    /**
     * Scans a number literal (integer or decimal).
     * After scanning, checks if followed by a currency code (TRY, USD, EUR, GBP).
     * If yes, creates a MONEY token; otherwise, creates a NUMBER token.
     */
    private void scanNumber() {
        // Consume digits
        while (isDigit(peek())) {
            advance();
        }
        
        // Check for decimal part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the '.'
            advance();
            
            // Consume decimal digits
            while (isDigit(peek())) {
                advance();
            }
        }
        
        // Parse the numeric value
        double value = Double.parseDouble(source.substring(start, current));
        
        // Check if followed by a currency code
        // Skip any whitespace first
        int savedCurrent = current;
        int savedLine = line;
        
        skipWhitespace();
        
        // Try to read currency code
        if (isAlpha(peek())) {
            int currencyStart = current;
            while (isAlpha(peek())) {
                advance();
            }
            String currency = source.substring(currencyStart, current);
            
            // Check if it's a valid currency
            if (currency.equals("TRY") || currency.equals("USD") || 
                currency.equals("EUR") || currency.equals("GBP")) {
                // It's a MONEY token!
                Money money = new Money(value, currency);
                addToken(TokenType.MONEY, money);
                return;
            } else {
                // Not a currency - restore position and treat as NUMBER
                current = savedCurrent;
                line = savedLine;
            }
        } else {
            // No currency code found - restore position
            current = savedCurrent;
            line = savedLine;
        }
        
        // It's just a NUMBER token
        addToken(TokenType.NUMBER, value);
    }
    
    /**
     * Scans an identifier or keyword.
     */
    private void scanIdentifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        
        String text = source.substring(start, current);
        
        // Check if it's a keyword
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        
        addToken(type);
    }
    
    /**
     * Skips a block comment.
     **/
    private void skipBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                // Found end of comment
                advance(); // consume *
                advance(); // consume /
                return;
            }
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        
        throw new RuntimeException("Unterminated block comment at line " + line);
    }
    
    /**
     * Skips whitespace characters (used when checking for currency codes).
     */
    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                advance();
            } else if (c == '\n') {
                line++;
                advance();
            } else {
                break;
            }
        }
    }
    
    /**
     * Advances to the next character and returns it.
     */
    private char advance() {
        return source.charAt(current++);
    }
    
    /**
     * Returns the current character without advancing.
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    /**
     * Returns the next character (lookahead by 1) without advancing.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    /**
     * Checks if the current character matches the expected character.
     * If yes, advances and returns true; otherwise returns false.
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        
        current++;
        return true;
    }
    
    /**
     * Checks if we've reached the end of the source code.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    /**
     * Checks if a character is a digit (0-9).
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    /**
     * Checks if a character is alphabetic or underscore.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    /**
     * Checks if a character is alphanumeric or underscore.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    /**
     * Adds a token without a value.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    /**
     * Adds a token with a value.
     */
    private void addToken(TokenType type, Object value) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, value, line));
    }
}
