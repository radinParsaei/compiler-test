package Main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private BaseClasses.CompilerBase compiler;
    private HashMap<String, String> lexerConfs;
    private ArrayList<String> removables;

    public Lexer(BaseClasses.CompilerBase compiler){
        this.lexerConfs = new HashMap<>();
        this.compiler = compiler;
        this.removables = new ArrayList<>();
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
        int i = 0;
        for (String toRemove : removables) {
            if (input.startsWith(toRemove)) {
                return new Token("REMOVABLE" + i, toRemove);
            }
            input = input.replace(toRemove, "");
            i++;
        }
        for (Map.Entry conf : lexerConfs.entrySet()) {
            if(Pattern.matches("^" + conf.getValue() + ".*", input)){
                return new Token((String) conf.getKey(), findFromText(input, conf.getKey().toString()));
            }
        }
        return new Token();
    }

    void addRemoveString(String toRemove){
        removables.add(toRemove);
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