import java.util.List;

/**
 * Parser for BudgetDSL.
 * Takes a list of tokens from the Lexer and checks the syntax.
 * For Part 1, this parser validates syntax and can print the parse tree structure.
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;  // Current position in token list
    
    /**
     * Creates a new Parser with the given tokens.
     * @param tokens List of tokens from the Lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    /**
     * Starts parsing the token stream.
     * This is the main entry point for parsing.
     */
    public void parse() {
        try {
            System.out.println("Parser: Starting to parse...");
            
            // Parse the budget declaration
            budgetDecl();
            
            // Check if we reached EOF properly
            if (!isAtEnd()) {
                throw new RuntimeException("Unexpected tokens after end of program");
            }
            
            System.out.println("Parser: Parsing completed successfully!");
            
        } catch (RuntimeException e) {
            System.err.println("Parse error: " + e.getMessage());
            throw e;
        }
    }
    
    // ===== HELPER METHODS FOR TOKEN NAVIGATION =====
    
    /**
     * Returns the current token without consuming it.
     * @return The current token
     */
    private Token current() {
        return tokens.get(current);
    }
    
    /**
     * Returns the previous token (the one we just consumed).
     * @return The previous token
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    /**
     * Consumes the current token and moves to the next one.
     * @return The token that was just consumed
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }
    
    /**
     * Checks if we've reached the end of the token stream.
     * @return true if current token is EOF
     */
    private boolean isAtEnd() {
        return current().getType() == TokenType.EOF;
    }
    
    // ===== HELPER METHODS FOR TOKEN CHECKING =====
    
    /**
     * Checks if the current token is of the given type.
     * Does NOT consume the token.
     * @param type The TokenType to check
     * @return true if current token matches the type
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return current().getType() == type;
    }
    
    /**
     * Checks if the current token is one of the given types.
     * Does NOT consume the token.
     * @param types Variable number of TokenTypes to check
     * @return true if current token matches any of the types
     */
    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * If the current token matches the given type, consume it and return true.
     * Otherwise, return false without consuming.
     * @param type The TokenType to match
     * @return true if matched and consumed
     */
    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }
    
    /**
     * If the current token matches any of the given types, consume it and return true.
     * Otherwise, return false without consuming.
     * @param types Variable number of TokenTypes to match
     * @return true if any matched and consumed
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Expects the current token to be of the given type.
     * If it matches, consumes it and returns it.
     * If it doesn't match, throws an error.
     * @param type The expected TokenType
     * @param errorMessage The error message if token doesn't match
     * @return The consumed token
     * @throws RuntimeException if token doesn't match
     */
    private Token expect(TokenType type, String errorMessage) {
        if (check(type)) {
            return advance();
        }
        
        throw error(current(), errorMessage);
    }
    
    /**
     * Creates a RuntimeException with a formatted error message.
     * Includes line number and token information.
     * @param token The token where the error occurred
     * @param message The error message
     * @return A RuntimeException with formatted message
     */
    private RuntimeException error(Token token, String message) {
        String errorMsg = String.format(
            "[Line %d] Error at '%s': %s",
            token.getLineNumber(),
            token.getText(),
            message
        );
        return new RuntimeException(errorMsg);
    }
    
    // ===== PARSING METHODS =====
    
    /**
     * Parses a budget declaration.
     * Grammar: budget IDENTIFIER { statement* }
     */
    private void budgetDecl() {
        // Expect 'budget' keyword
        expect(TokenType.BUDGET, "Expected 'budget' keyword");
        
        // Expect budget name (identifier)
        Token budgetName = expect(TokenType.IDENTIFIER, "Expected budget name");
        System.out.println("Budget: " + budgetName.getText());
        
        // Expect opening brace
        expect(TokenType.LEFT_BRACE, "Expected '{' after budget name");
        
        // Parse statements until we hit closing brace
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statement();
        }
        
        // Expect closing brace
        expect(TokenType.RIGHT_BRACE, "Expected '}' at end of budget");
    }
    
    /**
     * Parses a single statement.
     * Dispatches to specific statement parsing methods based on token type.
     */
    private void statement() {
        if (match(TokenType.INCOME)) {
            incomeStmt();
        } else if (match(TokenType.EXPENSE)) {
            expenseStmt();
        } else if (match(TokenType.LIMIT)) {
            // TODO: limitStmt();
            throw error(current(), "LIMIT statements not yet implemented");
        } else if (match(TokenType.PRINT)) {
            // TODO: printStmt();
            throw error(current(), "PRINT statements not yet implemented");
        } else if (check(TokenType.IDENTIFIER)) {
            // TODO: assignmentStmt();
            throw error(current(), "Assignment statements not yet implemented");
        } else {
            throw error(current(), "Unexpected statement");
        }
    }
    
    /**
     * Parses an income statement.
     * Grammar: income MONEY [as STRING]
     * Example: income 5000 TRY as "salary"
     */
    private void incomeStmt() {
        // Expect a MONEY token
        Token moneyToken = expect(TokenType.MONEY, "Expected money amount after 'income'");
        Money amount = (Money) moneyToken.getValue();
        
        // Check for optional "as" clause
        String label = null;
        if (match(TokenType.AS)) {
            Token labelToken = expect(TokenType.STRING, "Expected label after 'as'");
            label = (String) labelToken.getValue();
        }
        
        // Print the parsed statement (with indentation)
        if (label != null) {
            System.out.printf("  Income: %.2f %s as \"%s\"%n", 
                amount.getAmount(), amount.getCurrency(), label);
        } else {
            System.out.printf("  Income: %.2f %s%n", 
                amount.getAmount(), amount.getCurrency());
        }
    }
    
    /**
     * Parses an expense statement.
     * Grammar: expense MONEY [in STRING]
     * Example: expense 2000 TRY in "rent"
     */
    private void expenseStmt() {
        // Expect a MONEY token
        Token moneyToken = expect(TokenType.MONEY, "Expected money amount after 'expense'");
        Money amount = (Money) moneyToken.getValue();
        
        // Check for optional "in" clause
        String category = null;
        if (match(TokenType.IN)) {
            Token categoryToken = expect(TokenType.STRING, "Expected category after 'in'");
            category = (String) categoryToken.getValue();
        }
        
        // Print the parsed statement (with indentation)
        if (category != null) {
            System.out.printf("  Expense: %.2f %s in \"%s\"%n", 
                amount.getAmount(), amount.getCurrency(), category);
        } else {
            System.out.printf("  Expense: %.2f %s%n", 
                amount.getAmount(), amount.getCurrency());
        }
    }
    
    // Example method structure - you'll add these based on your grammar:
    
    // private void parseBudget() {
    //     expect(TokenType.BUDGET, "Expected 'budget' keyword");
    //     Token name = expect(TokenType.IDENTIFIER, "Expected budget name");
    //     expect(TokenType.LEFT_BRACE, "Expected '{' after budget name");
    //     
    //     // Parse budget body...
    //     
    //     expect(TokenType.RIGHT_BRACE, "Expected '}' at end of budget");
    // }
    
    // private void parseStatement() {
    //     if (match(TokenType.INCOME)) {
    //         parseIncome();
    //     } else if (match(TokenType.EXPENSE)) {
    //         parseExpense();
    //     } else if (match(TokenType.LIMIT)) {
    //         parseLimit();
    //     } else if (match(TokenType.PRINT)) {
    //         parsePrint();
    //     } else {
    //         throw error(current(), "Unexpected statement");
    //     }
    // }
}