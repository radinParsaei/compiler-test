package Main;

import java.util.ArrayList;

public class BaseClasses {
	public static class SyntaxTreeBase {
		private Object data;
		private String type = "";
		public Object getData() {
			return data;
		}
		
		public void setData(Object data) {
			this.data = data;
		}
		
		protected void setType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}

		@Override
		public String toString() {
			return this.getData().toString();
		}
	}
	
	public static abstract class CompilerBase {
		public String getInputCode() {
			return null;
		}

		public void initLexer(Lexer lexer) {
			
		}

		public ArrayList<String> listAll(){
			return new ArrayList<>();
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
}
