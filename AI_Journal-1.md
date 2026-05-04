Entry #1

Date: 2026-05-03, 14:00

Phase: Implementation - Basic Classes

AI tool: Claude Sonnet 4



Goal:

Create the foundational classes for BudgetDSL tokenization.



Prompt:

"Hi! I'm working on a programming languages course project (CSE 341) and 

I need help implementing a simple domain-specific language.



I'm creating BudgetDSL - a language for budget management. For Part 1, 

I need to build a Lexer and Parser in Java.



My language has these features:

\- Keywords: budget, income, expense, limit, print, if, then, else, end

\- Money literals: like 5000 TRY, 99.99 USD, 50 EUR, 25 GBP

\- Strings: \\"salary\\", \\"rent\\"

\- Numbers: 42, 3.14

\- Operators: +, -, \*, /, =, >, 



Can you help me create these basic classes first?

1\. TokenType.java (enum for all token types)

2\. Token.java (class to hold token info)

3\. Money.java (class for money values - amount + currency)



I'm using Java 17. Please keep it simple, I'm still learning!"



Response:

Claude provided three Java classes:



1\. TokenType.java - enum with all token categories (keywords, operators, 

&#x20;  literals, delimiters, EOF, and NEWLINE)



2\. Token.java - class with fields: type, text, value, line number

&#x20;  Also included helper methods is() and isOneOf() for cleaner parser code



3\. Money.java - class with amount (double) and currency (String)

&#x20;  Included arithmetic operations (add, subtract, multiply, divide)

&#x20;  Had currency validation and proper equals/hashCode



Accepted:

✅ TokenType enum structure - comprehensive and well-organized

✅ Token class with line tracking - essential for error messages

✅ Money class basic structure (amount + currency)

✅ Helper methods in Token - will make parser cleaner



Rejected/Modified:

✗ NEWLINE token in TokenType - unnecessary, I'll skip newlines in lexer

✗ Arithmetic operations in Money class - not needed for Part 1



I asked Claude to simplify and remove:

\- NEWLINE token type

\- add(), subtract(), multiply(), divide() methods from Money



Reasoning: Part 1 only parses code, doesn't execute it. Arithmetic 

operations would be unused code. Better to add them in Part 2 when 

implementing the interpreter.



Errors:

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

