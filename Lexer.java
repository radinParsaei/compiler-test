import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private CompilerBase compiler;
	private HashMap<String, String> lexerConfs = new HashMap<>();

	public Lexer(CompilerBase compiler){
		this.compiler = compiler;
	}

	public void add(String name, String regex){
		lexerConfs.put(name, "(" + regex + ")");
	}

	private String findFromText(String text, String regex) {
		Pattern p = Pattern.compile("^" + lexerConfs.get(regex));
		Matcher m = p.matcher(text);
		if (m.find()) {
			return m.group(0);
		}
		return "";
	}

	public Token getToken(String input){
		for (Map.Entry conf : lexerConfs.entrySet()) {
			if (!findFromText(input, conf.getKey().toString()).equals("")) {//Pattern.matches("^" + conf.getValue() + ".*", input)
				return new Token((String) conf.getKey(), findFromText(input, conf.getKey().toString()));
			}
		}
		return new Token();
	}

	public ArrayList<Token> lex(String input){
		String line = input;
		String previusInput;
		ArrayList<Token> tokens = new ArrayList<>();
		while(input.length() != 0){
			Token token = getToken(input);
			tokens.add(token);
			previusInput = input;
			input = input.substring(token.getText().length());
			if(previusInput.equals(input)){
				try {
					compiler.getClass().getMethod("syntaxError", int.class, String.class).invoke(compiler.getClass(), line.length() - input.length(), line);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				return new ArrayList<Token>();
			}
		}
		return tokens;
	}
}