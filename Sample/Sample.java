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
		lexer.add("NUM", "(-|\\+)?\\d+\\.?\\d*");
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
	}

	@ParserEvent("exp : NUM")
	public Object number(Parser parser) {
		return new SyntaxTree.Number(Double.parseDouble(parser.getTokens().get(0).getText()));
	}

	@ParserEvent("exp : TXT")
	public Object text(Parser parser) {
		return new SyntaxTree.Text(parser.getTokens().get(0).getText());
	}

	@ParserEvent("exp : exp OPERATIONS2 exp")
	public Object operations2(Parser parser) {
		if(Pattern.matches("\\+", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Plus((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("-", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Minus((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent("exp : exp OPERATIONS1 exp")
	public Object operations1(Parser parser) {
		if(Pattern.matches("\\*", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Multiply((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("/", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Division((SyntaxTreeBase) parser.getTokens().get(0).getObject(), (SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent("program : exp SET exp")
	public Object set(Parser parser) {
		return new SyntaxTree.SetVariable(parser.getTokens().get(0).getText(), (SyntaxTreeBase)parser.getTokens().get(2).getObject(), variables);
	}

	@ParserEvent("program : FUNC exp program END")
	public Object function(Parser parser) {
		return new SyntaxTree.SetFunction(parser.getTokens().get(1).getText(), (SyntaxTreeBase)parser.getTokens().get(2).getObject(), functions);
	}

	@ParserEvent("exp : ID")
	public Object variable(Parser parser) {
		return new SyntaxTree.Variable(variables, parser.getTokens().get(0).getText());
	}

	@ParserEvent("program : PRINT exp")
	public Object print(Parser parser) {
		return new SyntaxTree.Print(((SyntaxTreeBase)parser.getTokens().get(1).getObject()), "\n");
	}

	@ParserEvent("program : exp OPEN_PAREN CLOSE_PAREN")
	public Object callFunction(Parser parser) {
		return new SyntaxTree.CallFunction(parser.getTokens().get(0).getText(), functions);
	}

	@ParserEvent("program : REPEAT exp program END")
	public Object repeat(Parser parser) {
		return new SyntaxTree.Repeat((int)((SyntaxTreeBase)parser.getTokens().get(1).getObject()).getData(), ((SyntaxTreeBase)parser.getTokens().get(2).getObject()));
	}

	@ParserEvent("program : program program")
	public Object programs(Parser parser) {
		return new SyntaxTree.Programs((SyntaxTreeBase)parser.getTokens().get(0).getObject(), (SyntaxTreeBase)parser.getTokens().get(1).getObject());
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

	public ArrayList<String> listAll(){
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("number");
		arrayList.add("text");
		arrayList.add("variable");
		arrayList.add("operations1");
		arrayList.add("operations2");
		arrayList.add("print");
		arrayList.add("repeat");
		arrayList.add("set");
		arrayList.add("programs");
		arrayList.add("function");
		arrayList.add("callFunction");
		return arrayList;
	}

	public static void syntaxError(int errorChar, String line) {
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
