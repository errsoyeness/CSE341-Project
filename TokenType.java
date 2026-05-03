/**
 * TokenType enum defines all possible token types in BudgetDSL.
 * Each token represents a meaningful unit in the language (keywords, operators, literals, etc.)
 */
public enum TokenType {
    // Keywords
    BUDGET,
    INCOME,
    EXPENSE,
    LIMIT,
    PRINT,
    IF,
    THEN,
    ELSE,
    END,
    FOR,
    WHILE,
    FUNCTION,
    RETURN,
    
    // Special keywords for money operations
    AS,      // "as" in: income 5000 TRY as "salary"
    IN,      // "in" in: expense 2000 TRY in "rent"
    TO,      // "to" in: limit "food" to 1500 TRY
    
    // Literals
    MONEY,          // e.g., 5000 TRY, 99.99 USD
    STRING,         // e.g., "salary", "rent"
    NUMBER,         // e.g., 42, 3.14
    IDENTIFIER,     // e.g., monthly, remaining, total
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    ASSIGN,         // =
    EQUAL,          // ==
    NOT_EQUAL,      // !=
    GREATER,        // >
    LESS,           // <
    GREATER_EQUAL,  // >=
    LESS_EQUAL,     // <=
    
    // Delimiters
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    
    // Special
    EOF             // End of file
}
