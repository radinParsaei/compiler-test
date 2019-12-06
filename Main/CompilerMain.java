package Main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CompilerMain {
	public static void compile(BaseClasses.CompilerBase compiler) {
		Object compilerInstance = null;
		try {
			compilerInstance = compiler.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		Lexer lexer = new Lexer(compiler);
		compiler.initLexer(lexer);
		ArrayList<Token> result = lexer.lex(compiler.getInputCode());
		Parser parser = new Parser(result);
		parser.remove("IGNORE");
		compiler.afterLex(parser);
		try {
			compiler.parse(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 2; i++) {
				ArrayList<String> arrayList = (ArrayList<String>) compilerInstance.getClass().getMethod("listAll").invoke(compilerInstance);
				for (String method : arrayList) {
					String map;
					map = compilerInstance.getClass().getMethod(method, Parser.class).getDeclaredAnnotation(ParserEvent.class).value();
					parser.on(map.split(":")[1].trim(), map.split(":")[0].trim(), compilerInstance, method);
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
}
