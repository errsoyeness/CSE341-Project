import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Test runner for BudgetDSL test files.
 * Runs all .bdsl files in the tests directory.
 */
public class TestRunner {
    
    public static void main(String[] args) {
        String[] validTests = {
            "tests/example1.bdsl",
            "tests/example2.bdsl",
            "tests/example3.bdsl"
        };
        
        String[] errorTests = {
            "tests/error1.bdsl",
            "tests/error2.bdsl",
            "tests/error3.bdsl",
            "tests/error4.bdsl",
            "tests/error5.bdsl"
        };
        
        System.out.println("========================================");
        System.out.println("  BudgetDSL Test Runner");
        System.out.println("========================================\n");
        
        // Run valid tests
        System.out.println("=== VALID TESTS ===\n");
        for (String testFile : validTests) {
            runTest(testFile, true);
        }
        
        // Run error tests
        System.out.println("\n=== ERROR TESTS (Should Fail) ===\n");
        for (String testFile : errorTests) {
            runTest(testFile, false);
        }
    }
    
    private static void runTest(String filename, boolean shouldSucceed) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Test: " + filename);
        System.out.println("─────────────────────────────────────");
        
        try {
            // Read the source file
            String source = Files.readString(Path.of(filename));
            
            System.out.println("Source:");
            System.out.println(source);
            System.out.println();
            
            // Lex and parse
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            
            Parser parser = new Parser(tokens);
            parser.parse();
            
            if (shouldSucceed) {
                System.out.println("✓ PASS - Parsed successfully\n");
            } else {
                System.out.println("✗ FAIL - Expected error but parsing succeeded\n");
            }
            
        } catch (IOException e) {
            System.err.println("✗ FAIL - Could not read file: " + e.getMessage() + "\n");
        } catch (RuntimeException e) {
            if (!shouldSucceed) {
                System.out.println("✓ PASS - Correctly caught error:");
                System.out.println("  " + e.getMessage() + "\n");
            } else {
                System.err.println("✗ FAIL - Unexpected error:");
                System.err.println("  " + e.getMessage() + "\n");
            }
        }
    }
}
