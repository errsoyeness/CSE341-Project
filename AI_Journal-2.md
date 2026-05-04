Entry #2

Date: 2026-05-03, 15:30

Phase: Implementation - Lexer

AI tool: Claude Sonnet 4



Goal:

Implement the Lexer to tokenize BudgetDSL source code.



Prompt:

"Perfect! Now let's build the Lexer.



The Lexer should scan source code character by character and create tokens.



Important requirements:

1\. Skip whitespace and track line numbers (for error messages)

2\. Skip comments: // single-line and /\* \*/ multi-line

3\. Recognize keywords vs identifiers (\\"income\\" → INCOME, \\"my\_budget\\" → IDENTIFIER)

4\. Parse money literals as single tokens: \\"5000 TRY\\" → MONEY token with Money(5000.0, \\"TRY\\")

5\. Parse strings: \\"salary\\" → STRING token

6\. Parse numbers: 42 → NUMBER, 3.14 → NUMBER

7\. Handle operators: = → EQUAL, == → EQUAL\_EQUAL, >= → GREATER\_EQUAL

8\. Track line numbers for each token



Tricky part: distinguishing \\"5000 TRY\\" (money) from \\"5000\\" (number).

After scanning a number, check if the next word is TRY/USD/EUR/GBP.



Can you write Lexer.java?"



Response:

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

✅ Overall structure - well organized and readable

✅ Position save/restore trick for money parsing - elegant!

✅ HashMap for keywords - efficient O(1) lookup

✅ Line number tracking throughout

✅ Comment handling (both types)

✅ Helper methods (peek, advance, match) - clean code



Rejected/Modified:

✗ JavaDoc comment had syntax error: "Skips a block comment (/\* ... \*/)"

&#x20;  The /\* inside the comment caused compile error



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

\- Money literals parsed correctly: "5000 TRY" → single MONEY token ✅

\- Keywords recognized: "income" → INCOME token ✅

\- Line numbers tracked: each token knows its source line ✅



Reflection:

The Lexer is more complex than Token classes - about 360 lines vs 20-30 

lines each. The money literal parsing was the trickiest part.



AI's solution of saving/restoring position is smart - I wouldn't have 

thought of that approach myself. Shows how AI can suggest patterns that 

are common in compiler design.



Key learning: Be careful with nested comment syntax in JavaDoc. 

The error message "illegal start of type" wasn't obvious at first.



Testing confirmed everything works:

\- "5000 TRY" → MONEY ✅

\- "5000" followed by keyword → NUMBER ✅  

\- Comments skipped ✅

\- Line numbers correct ✅



Ready for Parser next (tomorrow). The Lexer successfully converts raw 

text into a clean list of tokens that the Parser can work with.

