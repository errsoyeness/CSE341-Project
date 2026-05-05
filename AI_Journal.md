\# CSE 341 Project - AI Usage Journal



Student: Enes ERSOY

Student ID: 220104004083

Course: CSE 341 - Concepts of Programming Languages

Semester: Spring 2026



\---



\## Part 1 Entries 





\---



Entry #: 1

Date: 2026-05-03

Phase: Implementation - Basic Classes

AI tool: Claude Sonnet 4



Goal:

Create the foundational classes for BudgetDSL tokenization.



Prompt (verbatim):

"Hi! I'm working on a programming languages course project (CSE 341) and 

I need help implementing a simple domain-specific language.



I'm creating BudgetDSL - a language for budget management. For Part 1, 

I need to build a Lexer and Parser in Java.



My language has these features:

\- Keywords: budget, income, expense, limit, print, if, then, else, end

\- Money literals: like 5000 TRY, 99.99 USD, 50 EUR, 25 GBP

\- Strings: "salary", "rent"

\- Numbers: 42, 3.14

\- Operators: +, -, \*, /, =, >, 



Can you help me create these basic classes first?

1\. TokenType.java (enum for all token types)

2\. Token.java (class to hold token info)

3\. Money.java (class for money values - amount + currency)



I'm using Java 17. Please keep it simple, I'm still learning!"



Response :

Claude provided three Java classes:



1\. TokenType.java - enum with all token categories (keywords, operators, 

&#x20;  literals, delimiters, EOF, and NEWLINE)



2\. Token.java - class with fields: type, text, value, line number

&#x20;  Also included helper methods is() and isOneOf() for cleaner parser code



3\. Money.java - class with amount (double) and currency (String)

&#x20;  Included arithmetic operations (add, subtract, multiply, divide)

&#x20;  Had currency validation and proper equals/hashCode



Accepted:

✓ TokenType enum structure - comprehensive and well-organized

✓ Token class with line tracking - essential for error messages

✓ Money class basic structure (amount + currency)

✓ Helper methods in Token - will make parser cleaner



Rejected / Modified:

✗ NEWLINE token in TokenType - unnecessary, I'll skip newlines in lexer

✗ Arithmetic operations in Money class - not needed for Part 1



I asked Claude to simplify and remove:

\- NEWLINE token type

\- add(), subtract(), multiply(), divide() methods from Money



Reasoning: Part 1 only parses code, doesn't execute it. Arithmetic 

operations would be unused code. Better to add them in Part 2 when 

implementing the interpreter.



Errors I Caught:

None initially. Code worked after I removed package declarations 

(testing in flat directory structure).



Reflection:

AI tends to add features proactively. The arithmetic operations in Money 

looked useful but aren't needed yet. Important lesson: distinguish 

between "nice to have" and "actually required right now."



For Part 1: just parse and display structure

For Part 2: actually execute and calculate



Keeping it minimal makes the code easier to understand and explain in 

the exam. Also teaches me to critically evaluate AI suggestions rather 

than blindly accepting everything.



Next: Use these classes to build the Lexer.



\---



Entry #: 2

Date: 2026-05-03

Phase: Implementation - Lexer

AI tool: Claude Sonnet 4



Goal:

Implement the Lexer to tokenize BudgetDSL source code.



Prompt :

"Perfect! Now let's build the Lexer.



The Lexer should scan source code character by character and create tokens.



Important requirements:

1\. Skip whitespace and track line numbers (for error messages)

2\. Skip comments: // single-line and /\* \*/ multi-line

3\. Recognize keywords vs identifiers ("income" → INCOME, "my\_budget" → IDENTIFIER)

4\. Parse money literals as single tokens: "5000 TRY" → MONEY token with Money(5000.0, "TRY")

5\. Parse strings: "salary" → STRING token

6\. Parse numbers: 42 → NUMBER, 3.14 → NUMBER

7\. Handle operators: = → EQUAL, == → EQUAL\_EQUAL, >= → GREATER\_EQUAL

8\. Track line numbers for each token



Tricky part: distinguishing "5000 TRY" (money) from "5000" (number).

After scanning a number, check if the next word is TRY/USD/EUR/GBP.



Can you write Lexer.java?"



Response (key part):

Claude provided a complete Lexer implementation (\~360 lines) including:



Main structure:

\- Constructor taking source string

\- scanTokens() method returning List<Token>

\- Keyword HashMap for fast lookup



Key methods:

\- scanToken() - handles each character type

\- scanNumber() - parses numbers and detects currency

\- scanString() - handles string literals with error checking

\- scanIdentifier() - distinguishes keywords from identifiers

\- skipBlockComment() - handles /\* \*/ comments



The money literal parsing was clever:

1\. Scan the number part (e.g., "5000")

2\. Save current position

3\. Skip whitespace  

4\. Read next word

5\. If it's TRY/USD/EUR/GBP → MONEY token

6\. If not → restore position, return NUMBER token



Accepted:

✓ Overall structure - well organized and readable

✓ Position save/restore trick for money parsing - elegant!

✓ HashMap for keywords - efficient O(1) lookup

✓ Line number tracking throughout

✓ Comment handling (both types)

✓ Helper methods (peek, advance, match) - clean code



Rejected / Modified:

✗ JavaDoc comment had syntax error: "Skips a block comment (/\* ... \*/)"

&#x20; The /\* inside the comment caused compile error



Fixed by changing to: "Skips multi-line comments"



Also removed package declarations to test in flat directory.



Errors I Caught:

1\. Compile error at line 247: "illegal start of type"

&#x20;  Cause: JavaDoc comment contained /\* ... \*/ which Java interpreted as 

&#x20;  actual comment syntax

&#x20;  Fix: Simplified comment text, removed the /\* ... \*/ example



2\. Had to test iteratively - first compile showed the error, then fixed



Testing process:

\- Compiled: javac \*.java

\- Ran: java LexerTest

\- Output showed 26 tokens correctly generated from test program

\- Money literals parsed correctly: "5000 TRY" → single MONEY token ✓

\- Keywords recognized: "income" → INCOME token ✓

\- Line numbers tracked: each token knows its source line ✓



Reflection:

The Lexer is more complex than Token classes - about 360 lines vs 20-30 

lines each. The money literal parsing was the trickiest part.



AI's solution of saving/restoring position is smart - I wouldn't have 

thought of that approach myself. Shows how AI can suggest patterns that 

are common in compiler design.



Key learning: Be careful with nested comment syntax in JavaDoc. 

The error message "illegal start of type" wasn't obvious at first.



Testing confirmed everything works:

\- "5000 TRY" → MONEY ✓

\- "5000" followed by keyword → NUMBER ✓  

\- Comments skipped ✓

\- Line numbers correct ✓



Ready for Parser next (tomorrow). The Lexer successfully converts raw 

text into a clean list of tokens that the Parser can work with.



\---



Entry #: 3

Date: 2026-05-04

Phase: Implementation - Parser

AI tool: Claude Sonnet 4



Goal:

Implement the Parser to check BudgetDSL syntax and display program structure.



Prompt #1 :

"Hey! I finished the Lexer yesterday, now I need the Parser.

I'm a bit confused about how to start. Can you help me with the basic

Parser structure first?

I need:



Parser class that takes List<Token> in constructor

A parse() method that starts parsing

Helper methods like advance(), current(), check(), expect()



Can you give me just the basic Parser skeleton first? I want to understand

the structure before adding the actual parsing logic."



Response to Prompt #1:

Worker provided a complete Parser skeleton (\~100 lines) with helper methods for token navigation and checking. Key methods included:

\- current() - returns current token without consuming

\- previous() - returns the token just consumed

\- advance() - moves to next token and returns previous

\- isAtEnd() - checks if we hit EOF

\- check(type) - checks current token type without consuming

\- match(type) - if matches, consumes and returns true

\- expect(type, msg) - must match or throw error with line number

\- error(token, msg) - creates formatted error message



The skeleton had a basic parse() method that would call budgetDecl(), and a TODO for actual parsing logic.



Accepted from Prompt #1:

✓ Clean separation between token navigation and parsing logic

✓ check() vs match() distinction - check() peeks, match() consumes

✓ expect() method for required tokens with good error messages

✓ Error formatting includes line numbers - crucial for debugging

✓ Helper methods make parsing code readable



Prompt #2 (verbatim):

"Perfect! I understand the structure now.

Let's start with the budget declaration. In my language, a program looks like:

budget monthly {

&#x20;   income 5000 TRY

&#x20;   expense 2000 TRY

}

So the parse() method should call budgetDecl(). Can you add budgetDecl()

method that expects BUDGET keyword, IDENTIFIER for name, LEFT\_BRACE,

loops through statements until RIGHT\_BRACE?

For now just leave a TODO for the statements - we'll add those next."



Response to Prompt #2:

Worker updated the Parser with budgetDecl() method that:

1\. Expects BUDGET keyword

2\. Expects IDENTIFIER and prints "Budget: \[name]"

3\. Expects LEFT\_BRACE

4\. While loop that calls statement() until RIGHT\_BRACE

5\. Expects RIGHT\_BRACE



Also added placeholder statement() method that just printed "Statement found".



Accepted from Prompt #2:

✓ budgetDecl() structure follows grammar correctly

✓ While loop pattern for handling multiple statements

✓ Good use of expect() for required tokens

✓ Print output shows structure being built



Errors I Caught After Prompt #2:



Bug #1: Infinite Loop

When I first tested, the output kept printing "Statement found" endlessly. The terminal filled with thousands of lines.



Cause: The statement() method wasn't consuming any tokens - it just printed and returned. So the while loop in budgetDecl() kept calling statement() on the same token forever.



Fix: Asked Worker to add advance() call in statement() to consume the token and move forward.



Lesson: Every parsing method must consume the tokens it's responsible for, otherwise you get stuck.



Prompt #3 (verbatim):

"Ok it's working but I see the problem - it's treating every single token

as a statement lol

Like "income 5000 TRY as salary" should be ONE statement but right now

it's printing 4 separate "Statement found" lines.

I need to actually parse income and expense properly. Can you add

incomeStmt() and expenseStmt() methods that consume all the tokens for

one complete statement?

For income: INCOME token, MONEY token, optional AS keyword + STRING

For expense: EXPENSE token, MONEY token, optional IN keyword + STRING

Also add indentation so it looks nice under "Budget: monthly""



Response to Prompt #3:

Worker completely rewrote statement() method to check token type and dispatch to appropriate parsing method. Added:



incomeStmt():

\- Consume INCOME keyword

\- Expect MONEY token, store value

\- Check if next is AS (using match)

\- If AS found, expect STRING for description

\- Print formatted: "  Income: \[amount] as \[description]"



expenseStmt():

\- Consume EXPENSE keyword

\- Expect MONEY token, store value

\- Check if next is IN (using match)

\- If IN found, expect STRING for category

\- Print formatted: "  Expense: \[amount] in \[category]"



statement() now has switch/if logic:

\- If INCOME → call incomeStmt()

\- If EXPENSE → call expenseStmt()

\- Else → error "Unexpected statement"



Accepted from Prompt #3:

✓ Proper statement-level parsing - one logical statement = one output line

✓ Optional keyword handling with match() - elegant solution

✓ Indentation makes structure visually clear

✓ Each parsing method consumes all its tokens

✓ Good separation of concerns - each statement type has own method



Errors I Caught After Prompt #3:



Bug #2: Statement Granularity

Before implementing specific parsing methods, I saw this output:

Statement found: INCOME

Statement found: MONEY

Statement found: AS

Statement found: STRING



Four separate statements when "income 5000 TRY as salary" should be ONE statement.



Cause: The statement() method was just calling advance() on each token individually, treating each token as a complete statement.



Fix: Implemented proper statement-level methods (incomeStmt, expenseStmt) that consume ALL tokens belonging to one logical statement.



Lesson: Statements are logical units composed of multiple tokens. The parser needs to group related tokens together, not process them one-by-one.



Testing Results:



Final test output:

=== Parsing ===

Parser: Starting to parse...

Budget: monthly

&#x20; Income: 5000,00 TRY as "salary"

&#x20; Expense: 2000,00 TRY in "rent"

Parser: Parsing completed successfully!



Verification checklist:

✓ Budget declaration parsed correctly

✓ Income with optional "as" description works

✓ Expense with optional "in" category works  

✓ Indentation shows hierarchy clearly

✓ No syntax errors

✓ Clean successful completion



Reflection:



The Parser was more complex than the Lexer but also more interesting. Several key concepts became clear:



1\. Recursive Descent Pattern:

Each grammar rule maps to a method. The method name matches the grammar rule name:

\- Grammar: `budgetDecl ::= BUDGET IDENTIFIER '{' statement\* '}'`

\- Code: `private void budgetDecl() { ... }`



The method consumes exactly the tokens specified by that grammar rule, then returns. This makes the code structure mirror the grammar - very intuitive once you see the pattern.



2\. Token Navigation Levels:

Understanding when to use each helper method was crucial:

\- check() = "What type is this?" - just peek, don't move

\- match() = "If it's type X, consume it" - conditional consume

\- expect() = "It must be type X" - required consume or error



For optional keywords like "as" and "in", match() is perfect - it checks and consumes only if present.



3\. Statements vs Tokens:

This was my biggest conceptual hurdle. Initially I thought of parsing as "processing tokens one by one." But that's wrong.



Parsing is "grouping tokens into logical units (statements)." 



A statement like `income 5000 TRY as "salary"` is composed of 4 tokens, but it's ONE semantic unit. The incomeStmt() method's job is to consume all 4 tokens and treat them as one thing.



4\. Error Messages:

The expect() method with line numbers is really well designed:

\[Line 3] Error at '}': Expected MONEY after 'income'



This tells you exactly where the error is and what was expected. Will be super helpful when creating error test cases.



5\. Building Incrementally:

Breaking the Parser into 3 separate prompts was the right approach:

\- Prompt #1: Skeleton only - understand the structure

\- Prompt #2: Budget parsing - get one thing working

\- Prompt #3: Statement types - add real functionality



Each step was testable. Each step built on previous understanding. Much better than trying to get everything in one giant prompt.



6\. The Power of match():

The optional keyword handling is elegant - this handles both cases without messy if-else checking.



What I Learned About AI:



AI is really good at standard patterns (recursive descent parsing is textbook), but I had to catch two bugs:

1\. Forgetting to consume tokens (infinite loop)

2\. Wrong granularity (token-level vs statement-level)



Both bugs taught me something important about parsing. The bugs weren't random - they showed gaps in my understanding. Fixing them deepened my knowledge.



Next Steps:



For Part 1, I still need to add:

\- print statement parsing

\- limit statement parsing  

\- if-then-else parsing

\- assignment parsing



But the foundation is solid. Each new statement type follows the same pattern:

1\. Check for keyword

2\. Consume keyword

3\. Expect required tokens

4\. Match optional tokens

5\. Print formatted output



Tomorrow I'll add these remaining statement types and create proper test files with error cases. The Parser structure makes adding new rules straightforward.



Key Insight:

Parsing feels like writing the grammar rules directly in code. The helper methods (check, match, expect) are like grammar notation (optional, required, alternatives). Once you see this connection, parser code becomes very readable.



\---



Entry #: 4

Date: 2026-05-05

Phase: Implementation - Parser Completion \& Testing

AI tool: Claude Sonnet 4



Goal: 

Complete the missing statement parsers and test the entire parser.

Yesterday I only implemented budget, income, and expense statements. 

Today I needed to add limit, print, if-then-else, and assignment statements.



Prompt :

"Hi, I'm working on BudgetDSL parser. Yesterday I implemented 

budgetDecl(), incomeStmt(), and expenseStmt(). 



Today I need to add these missing methods:



1\. limitStmt() - parses: limit "category" to <money>

2\. printStmt() - parses: print <identifier>  

3\. ifStmt() - parses: if <expr> then <stmts> \[else <stmts>] end

4\. assignment() - parses: <identifier> = <expr>



Here's my current Parser.java:

\[pasted my code]



Add these 4 methods and update statement() to call them."



Response (key part):

Claude added 4 new methods:

\- limitStmt(): parses "limit" STRING "to" MONEY

\- printStmt(): parses "print" IDENTIFIER  

\- ifStmt(): parses if-then-else-end structure with indented output

\- assignment(): parses IDENTIFIER "=" expression

\- expression(): also added a simple expression parser (literals + binary operators)



Claude also updated the statement() method with all the cases and provided 

TestRunner.java plus 8 test files (3 valid, 5 error).



Accepted:

✓ Used all 4 methods, parser is now complete

✓ TestRunner was very useful for automated testing

✓ Error handling is good - shows correct line numbers

✓ If statement's indented output clearly shows AST structure



Rejected / Modified:

✗ TestRunner initially couldn't find files because it was running from src/ 

&#x20; and trying to read tests/ directory. Fixed by running "java -cp src TestRunner" 

&#x20; from parent directory.

✗ Initially had flat file structure (lexer/, parser/ folders were empty). 

&#x20; Moving all .java files directly into src/ fixed compilation.



Errors I Caught:

During testing, I noticed the initial Parser.java was throwing "not yet implemented" 

errors for limit, print statements. Worker Claude's first code was incomplete. 

I requested again and got the full implementation.



Also got "cannot find symbol: class Lexer" compile error initially. This was 

due to package structure vs flat structure confusion. Switching to flat structure 

without packages fixed it.



Reflection (2-3 sentences):

Parser is fully complete and passing 8/8 tests! AI is fast at writing code but 

makes mistakes with environment setup (file paths, package structure). Testing 

is crucial - "AI gave me code" doesn't mean "it works".



Next up: writing PDFs - D1 (Design Spec), D3 (Test Report), D4 (Journal), 

D5 (Retrospective). Implementation is done, documentation remains.



