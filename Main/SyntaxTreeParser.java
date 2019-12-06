package Main;

public class SyntaxTreeParser {
	public static class Minus {
		public Object parse(Parser inp) {
			return new SyntaxTree.Minus((BaseClasses.SyntaxTreeBase)inp.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase)inp.getTokens().get(2).getObject());
		}
	}

	public static class Multiply {
		public Object parse(Parser inp) {
			return new SyntaxTree.Multiply((BaseClasses.SyntaxTreeBase)inp.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase)inp.getTokens().get(2).getObject());
		}
	}

	public static class Division {
		public Object parse(Parser inp) {
			return new SyntaxTree.Division((BaseClasses.SyntaxTreeBase)inp.getTokens().get(0).getObject(), (BaseClasses.SyntaxTreeBase)inp.getTokens().get(2).getObject());
		}
	}
}