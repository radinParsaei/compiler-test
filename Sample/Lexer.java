package Sample;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private BaseClasses.CompilerBase compiler;
    private HashMap<String, String> lexerConfs;
    private HashMap<String, String> nonRegexes;

    public Lexer(BaseClasses.CompilerBase compiler){
        this.lexerConfs = new HashMap<>();
        this.compiler = compiler;
        this.nonRegexes = new HashMap<>();
    }

    public void add(String name, String regex){
        lexerConfs.put(name, "(" + regex + ")");
    }

    private String findFromText(String text, String regex) {
        Pattern p = Pattern.compile(lexerConfs.get(regex));
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public Token getToken(String input){
        for (Map.Entry conf : nonRegexes.entrySet()) {
            if (input.startsWith((String) conf.getValue())) {
                return new Token((String) conf.getKey(), (String) conf.getValue());
            }
            input = input.replace((CharSequence) conf.getValue(), "");
        }
        for (Map.Entry conf : lexerConfs.entrySet()) {
            if(Pattern.matches("^" + conf.getValue() + ".*", input)){
                return new Token((String) conf.getKey(), findFromText(input, conf.getKey().toString()));
            }
        }
        return new Token();
    }

    void addNonRegex(String name, String string) {
        nonRegexes.put(name, string);
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