import java.util.List;

/**
 * Test program for the BudgetDSL Lexer.
 * Demonstrates tokenization of various BudgetDSL constructs.
 */
public class LexerTest {
    public static void main(String[] args) {
        // Example BudgetDSL program
        String source = """
            budget monthly {
                income 5000 TRY as "salary"
                expense 2000 TRY in "rent"
                expense 1500 TRY in "food"
                
                limit "food" to 1500 TRY
                
                remaining = 1500 TRY
                print remaining
            }
            """;
        
        System.out.println("=== BudgetDSL Lexer Test ===\n");
        System.out.println("Source Code:");
        System.out.println(source);
        System.out.println("\n=== Tokens ===\n");
        
        try {
            // Create lexer and scan tokens
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            
            // Print each token
            for (Token token : tokens) {
                System.out.println(token);
            }
            
            System.out.println("\n=== Success! ===");
            System.out.println("Total tokens: " + tokens.size());
            
        } catch (RuntimeException e) {
            System.err.println("Lexer error: " + e.getMessage());
        }
    }
}
