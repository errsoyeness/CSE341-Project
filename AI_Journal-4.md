\---

Entry #: 4

Date: 2026-05-05

Phase: Implementation - Parser Completion \& Testing

AI tool: Claude Sonnet 4



Goal: 

Complete the missing statement parsers and test the entire parser.

Yesterday I only implemented budget, income, and expense statements. 

Today I needed to add limit, print, if-then-else, and assignment statements.



Prompt (verbatim):

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

\---

