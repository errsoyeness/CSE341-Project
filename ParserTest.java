import java.util.List;

/**
 * Test program for the BudgetDSL Parser.
 * Demonstrates lexing and parsing of a simple budget program.
 */
public class ParserTest {
    public static void main(String[] args) {
        // Example BudgetDSL program
        String source = """
            budget monthly {
                income 5000 TRY as "salary"
                expense 2000 TRY in "rent"
            }
            """;
        
        System.out.println("=== BudgetDSL Parser Test ===\n");
        System.out.println("Source Code:");
        System.out.println(source);
        System.out.println("\n=== Lexing ===\n");
        
        try {
            // Step 1: Lex the source code
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            
            System.out.println("Tokens generated: " + tokens.size());
            for (Token token : tokens) {
                System.out.println("  " + token);
            }
            
            System.out.println("\n=== Parsing ===\n");
            
            // Step 2: Parse the tokens
            Parser parser = new Parser(tokens);
            parser.parse();
            
        } catch (RuntimeException e) {
            System.err.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
}