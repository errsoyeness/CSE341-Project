\### Entry #3



\*\*Date:\*\* 2026-05-04, 11:00

\*\*Phase:\*\* Implementation - Parser

\*\*AI Tool:\*\* Claude Sonnet 4



\*\*Goal:\*\*

Implement the Parser to check BudgetDSL syntax and display program structure.



\*\*Prompt #1 (Basic Structure):\*\*

Hey! I finished the Lexer yesterday, now I need the Parser.

I'm a bit confused about how to start. Can you help me with the basic

Parser structure first?

I need:



Parser class that takes List<Token> in constructor

A parse() method that starts parsing

Helper methods like advance(), current(), check(), expect()



Can you give me just the basic Parser skeleton first? I want to understand

the structure before adding the actual parsing logic.



\*\*Response to Prompt #1:\*\*

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



\*\*Accepted from Prompt #1:\*\*

✅ Clean separation between token navigation and parsing logic

✅ check() vs match() distinction - check() peeks, match() consumes

✅ expect() method for required tokens with good error messages

✅ Error formatting includes line numbers - crucial for debugging

✅ Helper methods make parsing code readable



\*\*Prompt #2 (Budget Declaration):\*\*

Perfect! I understand the structure now.

Let's start with the budget declaration. In my language, a program looks like:

budget monthly {

income 5000 TRY

expense 2000 TRY

}

So the parse() method should call budgetDecl(). Can you add budgetDecl()

method that expects BUDGET keyword, IDENTIFIER for name, LEFT\_BRACE,

loops through statements until RIGHT\_BRACE?

For now just leave a TODO for the statements - we'll add those next.



\*\*Response to Prompt #2:\*\*

Worker updated the Parser with budgetDecl() method that:

1\. Expects BUDGET keyword

2\. Expects IDENTIFIER and prints "Budget: \[name]"

3\. Expects LEFT\_BRACE

4\. While loop that calls statement() until RIGHT\_BRACE

5\. Expects RIGHT\_BRACE



Also added placeholder statement() method that just printed "Statement found".



\*\*Accepted from Prompt #2:\*\*

✅ budgetDecl() structure follows grammar correctly

✅ While loop pattern for handling multiple statements

✅ Good use of expect() for required tokens

✅ Print output shows structure being built



\*\*Errors I Caught After Prompt #2:\*\*



\*\*Bug #1: Infinite Loop\*\*

When I first tested, the output kept printing "Statement found" endlessly. The terminal filled with thousands of lines.



\*\*Cause:\*\* The statement() method wasn't consuming any tokens - it just printed and returned. So the while loop in budgetDecl() kept calling statement() on the same token forever.



\*\*Fix:\*\* Asked Worker to add advance() call in statement() to consume the token and move forward.



\*\*Lesson:\*\* Every parsing method must consume the tokens it's responsible for, otherwise you get stuck.



\*\*Prompt #3 (Income \& Expense):\*\*

Ok it's working but I see the problem - it's treating every single token

as a statement lol

Like "income 5000 TRY as salary" should be ONE statement but right now

it's printing 4 separate "Statement found" lines.

I need to actually parse income and expense properly. Can you add

incomeStmt() and expenseStmt() methods that consume all the tokens for

one complete statement?

For income: INCOME token, MONEY token, optional AS keyword + STRING

For expense: EXPENSE token, MONEY token, optional IN keyword + STRING

Also add indentation so it looks nice under "Budget: monthly"



\*\*Response to Prompt #3:\*\*

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



\*\*Accepted from Prompt #3:\*\*

✅ Proper statement-level parsing - one logical statement = one output line

✅ Optional keyword handling with match() - elegant solution

✅ Indentation makes structure visually clear

✅ Each parsing method consumes all its tokens

✅ Good separation of concerns - each statement type has own method



\*\*Errors I Caught After Prompt #3:\*\*



\*\*Bug #2: Statement Granularity\*\*

Before implementing specific parsing methods, I saw this output:

Statement found: INCOME

Statement found: MONEY

Statement found: AS

Statement found: STRING



Four separate statements when "income 5000 TRY as salary" should be ONE statement.



\*\*Cause:\*\* The statement() method was just calling advance() on each token individually, treating each token as a complete statement.



\*\*Fix:\*\* Implemented proper statement-level methods (incomeStmt, expenseStmt) that consume ALL tokens belonging to one logical statement.



\*\*Lesson:\*\* Statements are logical units composed of multiple tokens. The parser needs to group related tokens together, not process them one-by-one.



\*\*Testing Results:\*\*



Final test output:

=== Parsing ===

Parser: Starting to parse...

Budget: monthly

Income: 5000,00 TRY as "salary"

Expense: 2000,00 TRY in "rent"

Parser: Parsing completed successfully!



Verification checklist:

✅ Budget declaration parsed correctly

✅ Income with optional "as" description works

✅ Expense with optional "in" category works  

✅ Indentation shows hierarchy clearly

✅ No syntax errors

✅ Clean successful completion



\*\*Reflection:\*\*



The Parser was more complex than the Lexer but also more interesting. Several key concepts became clear:



\*\*1. Recursive Descent Pattern:\*\*

Each grammar rule maps to a method. The method name matches the grammar rule name:

\- Grammar: `budgetDecl ::= BUDGET IDENTIFIER '{' statement\* '}'`

\- Code: `private void budgetDecl() { ... }`



The method consumes exactly the tokens specified by that grammar rule, then returns. This makes the code structure mirror the grammar - very intuitive once you see the pattern.



\*\*2. Token Navigation Levels:\*\*

Understanding when to use each helper method was crucial:

\- \*\*check()\*\* = "What type is this?" - just peek, don't move

\- \*\*match()\*\* = "If it's type X, consume it" - conditional consume

\- \*\*expect()\*\* = "It must be type X" - required consume or error



For optional keywords like "as" and "in", match() is perfect - it checks and consumes only if present.



\*\*3. Statements vs Tokens:\*\*

This was my biggest conceptual hurdle. Initially I thought of parsing as "processing tokens one by one." But that's wrong.



Parsing is "grouping tokens into logical units (statements)." 



A statement like `income 5000 TRY as "salary"` is composed of 4 tokens, but it's ONE semantic unit. The incomeStmt() method's job is to consume all 4 tokens and treat them as one thing.



\*\*4. Error Messages:\*\*

The expect() method with line numbers is really well designed:

\[Line 3] Error at '}': Expected MONEY after 'income'



This tells you exactly where the error is and what was expected. Will be super helpful when creating error test cases.



\*\*5. Building Incrementally:\*\*

Breaking the Parser into 3 separate prompts was the right approach:

\- Prompt #1: Skeleton only - understand the structure

\- Prompt #2: Budget parsing - get one thing working

\- Prompt #3: Statement types - add real functionality



Each step was testable. Each step built on previous understanding. Much better than trying to get everything in one giant prompt.



\*\*6. The Power of match():\*\*

The optional keyword handling is elegant:

```java

if (match(TokenType.AS)) {

&#x20;   Token desc = expect(TokenType.STRING, "Expected description");

&#x20;   // use desc

}

```



This handles both cases:

\- "income 5000 TRY as salary" - match succeeds, description added

\- "income 5000 TRY" - match fails, skip description

No messy if-else checking needed.



\*\*What I Learned About AI:\*\*



AI is really good at standard patterns (recursive descent parsing is textbook), but I had to catch two bugs:

1\. Forgetting to consume tokens (infinite loop)

2\. Wrong granularity (token-level vs statement-level)



Both bugs taught me something important about parsing. The bugs weren't random - they showed gaps in my understanding. Fixing them deepened my knowledge.



\*\*Next Steps:\*\*



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



\*\*Key Insight:\*\*

Parsing feels like writing the grammar rules directly in code. The helper methods (check, match, expect) are like grammar notation (optional, required, alternatives). Once you see this connection, parser code becomes very readable.

