import java.util.ArrayList;

public abstract class CompilerBase {
	public String getInputCode() {
		return null;
	}

	public void initLexer(Lexer lexer) {
		
	}

	public ArrayList<String> listAll(){
		return new ArrayList<>();
	}
	
	public void afterLex(Parser result) {
			
	}
	
	public void parse(Parser tokens) {

	}
	
	public void afterParse(Parser result) {

	}
	
	public static void syntaxError(int errorChar, String line) {
	
	}
	
	public static void syntaxError(String line) {
	
	}
}
