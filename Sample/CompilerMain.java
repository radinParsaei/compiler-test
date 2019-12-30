import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CompilerMain {
	public static void compile(CompilerBase compiler) {
		Parser parser = lex(compiler);
		try {
			compiler.parse(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 2; i++) {
				ArrayList<String> arrayList = (ArrayList<String>) compiler.getClass().getMethod("listAll").invoke(compiler);
				for (String method : arrayList) {
					String map;
					map = compiler.getClass().getMethod(method, Parser.class).getDeclaredAnnotation(ParserEvent.class).value();
					parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), compiler, method);
					compiler.parse(parser);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			//Annotation ParserEvent not set for this function
		}
		try {
			compiler.parse(parser);
		} catch (Exception e) {
			e.printStackTrace();
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
