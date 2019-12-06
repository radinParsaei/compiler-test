package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Sample extends BaseClasses.CompilerBase {
	private int lines;
	private String fileName;
	
    public int getLines() {
		return lines;
	}

	public Sample(String fileName) {
		try {
			this.fileName = fileName;
		} catch(ArrayIndexOutOfBoundsException e){
			System.err.println("please set the file name");
			System.exit(1);
		}
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
		//Number
		lexer.add("NUM", "(-|\\+)?\\d+\\.?\\d*");
		//Text
		lexer.add("TXT", "\".*?(\")|\'.*?(\')");
		//operations
		lexer.add("OPERATIONS1", "\\*|\\/");
		lexer.add("OPERATIONS2", "\\-|\\+");
		//spaces (ignore)
		lexer.add("IGNORE", "[ \t]+");
		lexer.addRemoveString("\n");
		//print
		lexer.add("PRINT", "print ");
		//repeat
		lexer.add("REPEAT", "repeat ");
		//id
		lexer.add("ID", "([A-z]+\\d*_?)+");
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
			return new SyntaxTree.Plus((BaseClasses.SyntaxTreeBase) parser.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("-", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Minus((BaseClasses.SyntaxTreeBase) parser.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent("exp : exp OPERATIONS1 exp")
	public Object operations1(Parser parser) {
		if(Pattern.matches("\\*", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Multiply((BaseClasses.SyntaxTreeBase) parser.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase) parser.getTokens().get(2).getObject());
		} else if(Pattern.matches("/", parser.getTokens().get(1).getText())) {
			return new SyntaxTree.Division((BaseClasses.SyntaxTreeBase) parser.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase) parser.getTokens().get(2).getObject());
		}
		return null;
	}

	@ParserEvent("program : exp")
	public Object result(Parser parser) {
		return new SyntaxTree.Result((BaseClasses.SyntaxTreeBase) parser.getTokens().get(0).getObject());
	}

	@ParserEvent("program : PRINT exp")
	public Object print(Parser parser) {
		return new SyntaxTree.Print((BaseClasses.SyntaxTreeBase) parser.getTokens().get(1).getObject());
	}

	@ParserEvent("program : REPEAT exp REMOVABLE0 program")
	public Object repeat(Parser parser) {
		return new SyntaxTree.Repeat((BaseClasses.SyntaxTreeBase) parser.getTokens().get(3).getObject(), (BaseClasses.SyntaxTreeBase) parser.getTokens().get(1).getObject());
	}

	public void afterParse(Parser result) {
		if(result.getTokens().size() > 0) {
			for (Token token : result.getTokens()) {
				if (token.getName().equals("program")){
					if (token.getObject() instanceof SyntaxTree.Repeat){
                        int count = 1;
                        for (int i = 1; i < token.getObject().toString().split("\t").length; i++) {
                            count *= Integer.parseInt(token.getObject().toString().split("\t")[i]);
                        }
                        for (; count > 0; count--) {
                            System.out.println(token.getObject().toString().split("\t")[0]);
                        }
					} else {
						System.out.println(token.getObject());
					}
				} else if (token.getName().startsWith("REMOVABLE")){
                    //do nothing
				} else {
					syntaxError("Error in your syntax");
					break;
				}
			}
		} else {
			System.err.println("Error");
		}
	}

	public ArrayList<String> listAll(){
    	ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("number");
		arrayList.add("text");
		arrayList.add("operations1");
		arrayList.add("operations2");
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
