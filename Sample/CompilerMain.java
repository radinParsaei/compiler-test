import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.lang.reflect.Method;

public class CompilerMain {
	private static boolean doubleCheck = false;
	public void setDoubleCheck(boolean doubleCheck) {
		this.doubleCheck = doubleCheck;
	}

	public static void compile(CompilerBase compiler) {
		Parser parser = lex(compiler);
		try {
			compiler.parse(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<Integer, Method> methodsMap = new HashMap<>();
		for (Method method : compiler.getClass().getDeclaredMethods()) {
			try {
				methodsMap.put(method.getDeclaredAnnotation(ParserEvent.class).priority(), method);
			} catch (NullPointerException e) {
				//Annotation ParserEvent not set for this function
			}
		}
		TreeMap<Integer, Method> sortedMethodsMap = new TreeMap<>();
		sortedMethodsMap.putAll(methodsMap);
		try {
			ArrayList<String> arrayList = (ArrayList<String>) compiler.getClass().getMethod("listAll").invoke(compiler);
			for (Map.Entry<Integer, Method> entry : sortedMethodsMap.entrySet()) {
				String map = entry.getValue().getDeclaredAnnotation(ParserEvent.class).map();
				parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), (functionInput) -> {
					try{
						return entry.getValue().invoke(compiler, functionInput);
					} catch (InvocationTargetException | IllegalAccessException e) {
						return null;
					}
				});
				compiler.parse(parser);
			}
			if (doubleCheck) {
				for (Map.Entry<Integer, Method> entry : sortedMethodsMap.entrySet()) {
				String map = entry.getValue().getDeclaredAnnotation(ParserEvent.class).map();
				parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), (functionInput) -> {
					try{
						return entry.getValue().invoke(compiler, functionInput);
					} catch (InvocationTargetException | IllegalAccessException e) {
						return null;
					}
				});
				compiler.parse(parser);
			}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			//Annotation ParserEvent not set for this function
		}
		compiler.afterParse(parser);
	}

	public static Parser lex(CompilerBase compiler) {
		Lexer lexer = new Lexer(compiler);
		compiler.initLexer(lexer);
		ArrayList<Token> result = lexer.lex(compiler.getInputCode());
		Parser parser = new Parser(result);
		parser.remove("IGNORE");
		compiler.afterLex(parser);
		return parser;
	}
}
