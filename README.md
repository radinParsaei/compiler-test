# Mini compiler core

This project is a very small framework written in java you can make a compiler with it!

edit `Sample.java` file, `Main.java` and `SyntaxTree.java` to make your compiler

in Sample.java and in initLexer function you get a lexer object and you must to init and add configs for this object

after lexing the code function `afterLex(Parser)` runs. in this function you have inited parser with lexed code

in `parse` you must to parse code uses `parser.on()` function

and `afterParse()` function runs after parse the code in the sample we run the code in this function
