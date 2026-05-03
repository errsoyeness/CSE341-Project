/**
 * Token class represents a single token in BudgetDSL.
 * Each token contains its type, the original text, an optional value, and line number for error reporting.
 */
public class Token {
    private final TokenType type;
    private final String text;
    private final Object value;  // Can hold Money, String, Double, etc.
    private final int lineNumber;
    
    /**
     * Creates a new Token.
     * @param type The type of the token
     * @param text The original text from the source code
     * @param value The parsed value (e.g., Money object for MONEY tokens, Double for NUMBER)
     * @param lineNumber The line number where this token appears (for error messages)
     */
    public Token(TokenType type, String text, Object value, int lineNumber) {
        this.type = type;
        this.text = text;
        this.value = value;
        this.lineNumber = lineNumber;
    }
    
    /**
     * Creates a Token without a value (for keywords, operators, etc.).
     * @param type The type of the token
     * @param text The original text from the source code
     * @param lineNumber The line number where this token appears
     */
    public Token(TokenType type, String text, int lineNumber) {
        this(type, text, null, lineNumber);
    }
    
    /**
     * Gets the token type.
     * @return The TokenType
     */
    public TokenType getType() {
        return type;
    }
    
    /**
     * Gets the original text of the token.
     * @return The text as it appeared in source code
     */
    public String getText() {
        return text;
    }
    
    /**
     * Gets the parsed value of the token.
     * For MONEY tokens, this is a Money object.
     * For NUMBER tokens, this is a Double.
     * For STRING tokens, this is a String (without quotes).
     * For most other tokens, this may be null.
     * @return The value object, or null if not applicable
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * Gets the line number where this token appears.
     * Useful for error reporting.
     * @return The line number (1-indexed)
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Returns a string representation of this token.
     * Useful for debugging and displaying token streams.
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token{");
        sb.append("type=").append(type);
        sb.append(", text='").append(text).append("'");
        if (value != null) {
            sb.append(", value=").append(value);
        }
        sb.append(", line=").append(lineNumber);
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Checks if this token is of a specific type.
     * Convenience method for parser checks.
     * @param type The TokenType to check against
     * @return true if this token matches the given type
     */
    public boolean is(TokenType type) {
        return this.type == type;
    }
    
    /**
     * Checks if this token is one of several types.
     * Convenience method for parser checks.
     * @param types Variable number of TokenTypes to check against
     * @return true if this token matches any of the given types
     */
    public boolean isOneOf(TokenType... types) {
        for (TokenType t : types) {
            if (this.type == t) {
                return true;
            }
        }
        return false;
    }
}
