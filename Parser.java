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
            limitStmt();
        } else if (match(TokenType.PRINT)) {
            printStmt();
        } else if (match(TokenType.IF)) {
            ifStmt();
        } else if (check(TokenType.IDENTIFIER)) {
            assignment();
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
    
    /**
     * Parses a limit statement.
     * Grammar: limit STRING to MONEY
     * Example: limit "food" to 1500 TRY
     */
    private void limitStmt() {
        // Expect a STRING (category name)
        Token categoryToken = expect(TokenType.STRING, "Expected category name after 'limit'");
        String category = (String) categoryToken.getValue();
        
        // Expect "to" keyword
        expect(TokenType.TO, "Expected 'to' after category name");
        
        // Expect a MONEY token
        Token moneyToken = expect(TokenType.MONEY, "Expected money amount after 'to'");
        Money amount = (Money) moneyToken.getValue();
        
        // Print the parsed statement (with indentation)
        System.out.printf("  Limit: \"%s\" to %.2f %s%n", 
            category, amount.getAmount(), amount.getCurrency());
    }
    
    /**
     * Parses a print statement.
     * Grammar: print IDENTIFIER
     * Example: print remaining
     */
    private void printStmt() {
        // Expect an IDENTIFIER
        Token varToken = expect(TokenType.IDENTIFIER, "Expected variable name after 'print'");
        String varName = varToken.getText();
        
        // Print the parsed statement (with indentation)
        System.out.printf("  Print: %s%n", varName);
    }
    
    /**
     * Parses an if statement.
     * Grammar: if EXPR then STATEMENT* [else STATEMENT*] end
     * Example: if balance > 1000 TRY then print "good" else print "bad" end
     */
    private void ifStmt() {
        // Parse the condition expression
        System.out.print("  If: ");
        expression();
        System.out.println();
        
        // Expect "then" keyword
        expect(TokenType.THEN, "Expected 'then' after condition");
        
        // Parse statements in the "then" block
        System.out.println("    Then:");
        while (!check(TokenType.ELSE) && !check(TokenType.END) && !isAtEnd()) {
            System.out.print("    ");
            statement();
        }
        
        // Optional "else" block
        if (match(TokenType.ELSE)) {
            System.out.println("    Else:");
            while (!check(TokenType.END) && !isAtEnd()) {
                System.out.print("    ");
                statement();
            }
        }
        
        // Expect "end" keyword
        expect(TokenType.END, "Expected 'end' to close if statement");
    }
    
    /**
     * Parses an assignment statement.
     * Grammar: IDENTIFIER = EXPR
     * Example: remaining = 1500 TRY
     */
    private void assignment() {
        // Get the variable name
        Token varToken = expect(TokenType.IDENTIFIER, "Expected variable name");
        String varName = varToken.getText();
        
        // Expect "=" operator
        expect(TokenType.ASSIGN, "Expected '=' after variable name");
        
        // Parse the expression on the right side
        System.out.print("  Assignment: " + varName + " = ");
        expression();
        System.out.println();
    }
    
    /**
     * Parses an expression (simplified for Part 1).
     * For now, just consumes tokens until we hit a statement terminator.
     * In Part 2, you'll implement proper expression parsing with operators.
     */
    private void expression() {
        // For Part 1, just consume the expression tokens and print them
        // This is a simplified version - Part 2 will have proper expression parsing
        
        if (check(TokenType.MONEY)) {
            Token moneyToken = advance();
            Money amount = (Money) moneyToken.getValue();
            System.out.print(amount.getAmount() + " " + amount.getCurrency());
        } else if (check(TokenType.NUMBER)) {
            Token numToken = advance();
            System.out.print(numToken.getValue());
        } else if (check(TokenType.STRING)) {
            Token strToken = advance();
            System.out.print("\"" + strToken.getValue() + "\"");
        } else if (check(TokenType.IDENTIFIER)) {
            Token idToken = advance();
            System.out.print(idToken.getText());
        } else {
            throw error(current(), "Expected expression");
        }
        
        // Handle binary operators (simplified)
        while (match(TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY, 
                     TokenType.DIVIDE, TokenType.GREATER, TokenType.LESS,
                     TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.GREATER_EQUAL,
                     TokenType.LESS_EQUAL)) {
            Token operator = previous();
            System.out.print(" " + operator.getText() + " ");
            
            // Parse right side
            if (check(TokenType.MONEY)) {
                Token moneyToken = advance();
                Money amount = (Money) moneyToken.getValue();
                System.out.print(amount.getAmount() + " " + amount.getCurrency());
            } else if (check(TokenType.NUMBER)) {
                Token numToken = advance();
                System.out.print(numToken.getValue());
            } else if (check(TokenType.IDENTIFIER)) {
                Token idToken = advance();
                System.out.print(idToken.getText());
            }
        }
    }
}