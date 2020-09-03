import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.lang.reflect.Method;

public class CompilerMain {
	private static boolean doubleCheck = false;
	public static void setDoubleCheck(boolean doubleCheck2) {
		doubleCheck = doubleCheck2;
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
			for (compiler.setCounter(0); compiler.getCounter() <= sortedMethodsMap.size(); compiler.increaseCounter()) {
				if (sortedMethodsMap.get(compiler.getCounter()) == null) {
					continue;
				}
				String map = sortedMethodsMap.get(compiler.getCounter()).getDeclaredAnnotation(ParserEvent.class).map();
				int tmp = compiler.getCounter();
				parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), (functionInput) -> {
					try {
						return sortedMethodsMap.get(tmp).invoke(compiler, functionInput);
					} catch (InvocationTargetException | IllegalAccessException e) {
						return null;
					}
				});
				compiler.parse(parser);
			}
			if (doubleCheck) {
				for (compiler.setCounter(0); compiler.getCounter() <= sortedMethodsMap.size(); compiler.increaseCounter()) {
					if (sortedMethodsMap.get(compiler.getCounter()) == null) {
						continue;
					}
					String map = sortedMethodsMap.get(compiler.getCounter()).getDeclaredAnnotation(ParserEvent.class).map();
					int tmp = compiler.getCounter();
					parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), (functionInput) -> {
						try {
							return sortedMethodsMap.get(tmp).invoke(compiler, functionInput);
						} catch (InvocationTargetException | IllegalAccessException e) {
							return null;
						}
					});
					compiler.parse(parser);
				}
			}
		} catch (IllegalArgumentException e) {
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
