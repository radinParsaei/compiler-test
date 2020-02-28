import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Sample extends CompilerBase {
	private int lines;
	private String fileName;
	private HashMap<String, SyntaxTreeBase> variables = new HashMap<>();
	private HashMap<String, SyntaxTreeBase> functions = new HashMap<>();
	private boolean isShell;
	private boolean recheckOperators = false;
	
	public int getLines() {
		return lines;
	}

	public Sample(String fileName, boolean isShell) {
		this.fileName = fileName;
		this.isShell = isShell;
	}

	public String getInputCode() {
		if (isShell) {
			try {
				System.out.print("> ");
				return new Scanner(System.in).nextLine();
			} catch (java.util.NoSuchElementException e) {
				System.out.println("Running file [if available] (for closing use ctrl+c)");
			}
		}
		File file = new File(fileName);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.err.println("Can\'t open the file");
			System.exit(1);
		}
		scanner.useDelimiter("\\Z");
		return scanner.next();
	}

	public void initLexer(Lexer lexer) {
		//Text
		lexer.add("TXT", "\".*?(\")|\'.*?(\')");
		//Number
		lexer.add("NUM", "\\d+\\.?\\d*");
		//operations
		lexer.add("OPERATIONS1", "\\*|\\/");
		lexer.add("OPERATIONS2", "\\-|\\+");
		//spaces (ignore)
		lexer.add("IGNORE", "[ \\s]+");
		//function
		lexer.add("FUNC", "function ");
		//print
		lexer.add("PRINT", "print ");
		//repeat
		lexer.add("REPEAT", "repeat ");
		//end
		lexer.add("END", "end");
		//id
		lexer.add("ID", "([A-Za-z]+\\d*_*)+");
		//set
		lexer.add("SET", "=");
		//comment
		lexer.add("COMMENT", new CommentStringChecker());
		//parentheses
		lexer.add("OPEN_PAREN", "\\(");
		lexer.add("CLOSE_PAREN", "\\)");
	}

	public void afterLex(Parser result) {
		result.remove("COMMENT");
	}

	public void parse(Parser parser) {
//    	parser.on("NUM", "exp", (parser1) -> new SyntaxTree.Number(Double.parseDouble(parser1.getTokens().get(0).getText())));
		if (recheckOperators) {
			recheckOperators = false;
			parser.on("exp OPERATIONS1 exp", "exp", (parser1) -> operations1(parser1));
			parser.on("exp OPERATIONS2 exp", "exp", (parser1) -> operations2(parser1));
		}
	}

	@ParserEvent(map = "exp : NUM", priority = 0)
	public Object number(Parser parser) {
		return new SyntaxTree.Number(Double.parseDouble(parser.getTokens().get(0).getText()));
	}

	@ParserEvent(map = "exp : TXT", priority = 1)
	public Object text(Parser parser) {
		return new SyntaxTree.Text(parser.getTokens().get(0).getText());
	}

	@ParserEvent(map = "exp : ID", priority = 2)
	public Object variable(Parser parser) {
		return new SyntaxTree.Variable(variables, parser.getTokens().get(0).getText());
	}

	@ParserEvent(map = "exp : exp OPERATIONS1 OPERATIONS2 exp", priority = 3)
	public Object operations1WithPositiveAndNegative(Parser parser) {
		if(Pattern.matches("\\*", parser.getTokens().get(1).getText())) {
			if (parser.getTokens().get(2).getText().equals("-")) {
				return new SyntaxTree.Multiply((SyntaxTreeBase) parser.getTokens().get(0).getObject(), new SyntaxTree.Minus(new SyntaxTree.Number(0), (SyntaxTreeBase) parser.getTokens().get(3).getObject()));
			} else {
				return new SyntaxTree.Multiply((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(3).getObject());
			}
		} else if(Pattern.matches("/", parser.getTokens().get(1).getText())) {
			if (parser.getTokens().get(2).getText().equals("-")) {
				return new SyntaxTree.Division((SyntaxTreeBase) parser.getTokens().get(0).getObject(), new SyntaxTree.Minus(new SyntaxTree.Number(0), (SyntaxTreeBase) parser.getTokens().get(3).getObject()));
			} else {
				return new SyntaxTree.Division((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(3).getObject());
			}
		}
		return null;
	}

	@ParserEvent(map = "exp : exp OPERATIONS1 exp", priority = 4)
	public Object operations1(Parser parser) {
		if(Pattern.matches("\\*", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Multiply((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("/", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Division((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent(map = "exp : exp OPERATIONS2 exp", priority = 5)
	public Object operations2(Parser parser) {
		if(Pattern.matches("\\+", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Plus((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("-", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Minus((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent(map = "exp : OPERATIONS2 exp", priority = 6)
	public Object positiveAndNegative(Parser parser) {
		recheckOperators = true;
		if (parser.getTokens().get(0).getText().equals("-")) {
			return new SyntaxTree.Minus(new SyntaxTree.Number(0), (SyntaxTreeBase) parser.getTokens().get(1).getObject());
		}
		return parser.getTokens().get(1).getObject();
	}

	@ParserEvent(map = "program : PRINT exp", priority = 7)
	public Object print(Parser parser) {
		return new SyntaxTree.Print(((SyntaxTreeBase)parser.getTokens().get(1).getObject()), "\n");
	}

	@ParserEvent(map = "program : REPEAT exp program END", priority = 8)
	public Object repeat(Parser parser) {
		return new SyntaxTree.Repeat((int)((SyntaxTreeBase)parser.getTokens().get(1).getObject()).getData(), ((SyntaxTreeBase)parser.getTokens().get(2).getObject()));
	}

	@ParserEvent(map = "program : exp SET exp", priority = 9)
	public Object set(Parser parser) {
		return new SyntaxTree.SetVariable(parser.getTokens().get(0).getText(), (SyntaxTreeBase)parser.getTokens().get(2).getObject(), variables);
	}

	@ParserEvent(map = "program : program program", priority = 10)
	public Object programs(Parser parser) {
		return new SyntaxTree.Programs((SyntaxTreeBase)parser.getTokens().get(0).getObject(), (SyntaxTreeBase)parser.getTokens().get(1).getObject());
	}

	@ParserEvent(map = "program : FUNC exp program END", priority = 11)
	public Object function(Parser parser) {
		return new SyntaxTree.SetFunction(parser.getTokens().get(1).getText(), (SyntaxTreeBase)parser.getTokens().get(2).getObject(), functions);
	}

	@ParserEvent(map = "program : exp OPEN_PAREN CLOSE_PAREN", priority = 12)
	public Object callFunction(Parser parser) {
		return new SyntaxTree.CallFunction(parser.getTokens().get(0).getText(), functions);
	}

	public void afterParse(Parser result) {
		for (Token token : result.getTokens()) {
			if (token.getName().equals("program")) {
				((SyntaxTreeBase)token.getObject()).getRunnable().run();
			} else {
				System.out.println("Syntax is:\n" + result);
				syntaxError("Syntax Error");
				break;
			}
		}
	}

	public static void syntaxError(int errorChar, String line) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\n') {
				errorChar++;
			}
		}
		System.err.println(line.replace("\n", "\\n"));
		for(; errorChar > 0; errorChar--) {
			System.err.print(" ");
		}
		System.err.println("^");
	}
	
	public static void syntaxError(String line) {
		System.err.println("ERROR:\t" + line);
	}
}
