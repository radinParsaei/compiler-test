import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final CompilerBase compiler;
    private boolean error = true;
    private final LinkedHashMap<String, String> lexerConfs = new LinkedHashMap<>();
    private final LinkedHashMap<String, StringCheckerBase> lexerConfsWithStringChecker = new LinkedHashMap<>();
    private int line = 1;

    public void setError(boolean error) {
        this.error = error;
    }

    private String addStrings(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String item : strings) {
            builder.append(item);
        }
        return builder.toString();
    }

    public Lexer(CompilerBase compiler) {
        this.compiler = compiler;
    }

    public void add(String name, String regex) {
        lexerConfs.put(name, addStrings("(", regex, ")"));
    }

    public void add(String name, StringCheckerBase checker) {
        lexerConfsWithStringChecker.put(name, checker);
    }

    private String findFromText(String text, String regex) {
        Pattern p = Pattern.compile(addStrings("^", lexerConfs.get(regex)));
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public Token getToken(String input) {
        for (Map.Entry conf : lexerConfsWithStringChecker.entrySet()) {
            if (((StringCheckerBase) conf.getValue()).check(input)) {
                String text = ((StringCheckerBase) conf.getValue()).getText(input);
                for (char c : text.toCharArray())
                    if (c == '\n') line++;
                return new Token((String) conf.getKey(), text, line);
            }
        }
        for (Map.Entry conf : lexerConfs.entrySet()) {
            String result = findFromText(input, conf.getKey().toString());
            if (!result.equals("")) {
                for (char c : result.toCharArray())
                    if (c == '\n') line++;
                return new Token((String) conf.getKey(), result, line);
            }
        }
        return new Token();
    }

    public ArrayList<Token> lex(String input) {
        String line = input;
        String previousInput;
        ArrayList<Token> tokens = new ArrayList<>();
        while (input.length() != 0) {
            Token token = getToken(input);
            tokens.add(token);
            previousInput = input;
            input = input.substring(token.getText().length());
            if (previousInput.equals(input)) {
                if (error) {
                    if (Targets.isWeb) {
                        Targets.tokenizerError(line.length() - input.length(), line);
                    } else {
                        try {
                            compiler.getClass().getMethod("syntaxError", int.class, String.class).invoke(compiler.getClass(), line.length() - input.length(), line);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    tokens.add(new Token("", input.substring(0, 1)));
                    input = input.substring(1);
                    tokens.remove(tokens.size() - 2);
                }
                if (error) return new ArrayList<Token>();
            }
        }
        return tokens;
    }
}
