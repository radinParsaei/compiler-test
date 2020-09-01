import java.util.ArrayList;

public abstract class CompilerBase {
	private int counter;

	int getCounter() {
		return counter;
	}

	void setCounter(int counter) {
		this.counter = counter - 1;
	}

	void increaseCounter() {
		this.counter++;
	}

	public String getInputCode() {
		return null;
	}

	public void initLexer(Lexer lexer) {

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
