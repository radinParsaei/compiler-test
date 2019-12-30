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
	
	public int getLines() {
		return lines;
	}

	public Sample(String fileName) {
		this.fileName = fileName;
	}

	public Sample() {
	}

	public String getInputCode() {
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
		//print
		lexer.add("PRINT", "print ");
		//repeat
		lexer.add("REPEAT", "repeat ");
		//id
		lexer.add("ID", "([A-Za-z]+\\d*_*)+");
		//set
		lexer.add("SET", "=");
	}

	public void afterLex(Parser result) {}

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

	@ParserEvent("program : ID SET exp")
	public Object set(Parser parser) {
		return new Runnable(){
			@Override
			public void run() {
				variables.put(parser.getTokens().get(0).getText(), ((SyntaxTreeBase)parser.getTokens().get(2).getObject()));
			}
		};
	}

	@ParserEvent("exp : ID")
	public Object variable(Parser parser) {
		return new SyntaxTree.Variable(variables, parser.getTokens().get(0).getText());
	}

	@ParserEvent("program : exp")
	public Object result(Parser parser) {
		return new Runnable(){
			@Override
			public void run() {
				System.out.println("Value is\t" + ((SyntaxTreeBase)parser.getTokens().get(0).getObject()));
			}
		};
	}

	@ParserEvent("program : PRINT exp")
	public Object print(Parser parser) {
		return new Runnable(){
			@Override
			public void run() {
				System.out.println(((SyntaxTreeBase)parser.getTokens().get(1).getObject()));
			}
		};
	}

	@ParserEvent("program : REPEAT exp NEW_LINE program")
	public Object repeat(Parser parser) {
		// return new SyntaxTree.Repeat((SyntaxTreeBase) parser.getTokens().get(3).getObject(), (SyntaxTreeBase) parser.getTokens().get(1).getObject());
		return new Runnable(){
			@Override
			public void run() {
				for (int i = 0; i < Integer.parseInt(parser.getTokens().get(1).getObject().toString()); i++) {
					((Runnable)parser.getTokens().get(3).getObject()).run();
				}
			}
		};
	}

	public void afterParse(Parser result) {
		result.remove("NEW_LINE");
		for (Token token : result.getTokens()) {
			if (token.getName().equals("program")) {
				((Runnable)token.getObject()).run();
			} else {
				syntaxError("Syntax Error");
				break;
			}
		}
	}

	public ArrayList<String> listAll(){
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("number");
		arrayList.add("text");
		arrayList.add("operations1");
		arrayList.add("operations2");
		arrayList.add("set");
		arrayList.add("variable");
		arrayList.add("print");
		arrayList.add("repeat");
		arrayList.add("result");
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
