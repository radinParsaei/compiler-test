import java.util.HashMap;

public class SyntaxTree {
	public static class Number extends SyntaxTreeBase {
		public Number(java.lang.Number data) {
			this.setData(data);
			if(((java.lang.Number) this.getData()).doubleValue() == ((java.lang.Number) this.getData()).intValue()){
				this.setData(((java.lang.Number)this.getData()).intValue());
			}
			this.setType("Number");
		}
	}
	
	public static class Text extends SyntaxTreeBase {
		public Text(String data) {
			this.setData(data.substring(1, data.length() - 1));
			this.setType("Text");
		}
	}
	
	public static class Plus extends SyntaxTreeBase {
		public Plus(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() + ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.getData() == ((Double)this.getData()).intValue()){
					this.setData(((Double) this.getData()).intValue());
				}
				this.setType("Number");
			} else {
				this.setData(value1.toString() + value2.toString());
				this.setType("Text");
			}
		}
	}

	public static class Minus extends SyntaxTreeBase {
		public Minus(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() - ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.getData() == ((Double)this.getData()).intValue()){
					this.setData(((Double) this.getData()).intValue());
				}
				this.setType("Number");
			} else {
				this.setData(value1.toString().replace(value2.toString(), ""));
				this.setType("Text");
			}
		}
	}
	
	public static class Multiply extends SyntaxTreeBase {
		private String stringMultiply(String string, int times) {
			String result = "";
			for(int i = 0; i < times; i++) {
				result += string;
			}
			return result;
		}

		public Multiply(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			if(value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() * ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.getData() == ((Double)this.getData()).intValue()){
					this.setData(((Double) this.getData()).intValue());
				}
				this.setType("Number");
			} else if((value1.getType().equals("Number") && value2.getType().equals("Text"))){
				Double count;
				count = ((java.lang.Number)value1.getData()).doubleValue();
				this.setData(this.stringMultiply(value2.getData().toString(), count.intValue()));
				this.setType("Text");
			} else if((value1.getType().equals("Text") && value2.getType().equals("Number"))){
				Double count;
				count = ((java.lang.Number)value2.getData()).doubleValue();
				this.setData(this.stringMultiply(value1.getData().toString(), count.intValue()));
				this.setType("Text");
			} else {
				Sample.syntaxError("Can not calculate TEXT MULTIPLY TEXT");
				this.setType("Text");
				this.setData("None");
			}
		}
	}

	public static class Division extends SyntaxTreeBase {
		public Division(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			if (value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setData(((java.lang.Number) value1.getData()).doubleValue() / ((java.lang.Number) value2.getData()).doubleValue());
				if ((Double) this.getData() == ((Double) this.getData()).intValue()) {
					this.setData(((Double) this.getData()).intValue());
				}
				this.setType("Number");
			} else {
				Sample.syntaxError("Can not calculate TEXT DIVISION TEXT");
				this.setType("Text");
				this.setData("None");
			}
		}
	}

	public static class Variable extends SyntaxTreeBase {
		private HashMap<String, SyntaxTreeBase> variables;
		private String variableName;
		public Variable(HashMap variables, String variableName) {
			this.variables = variables;
			this.variableName = variableName;
		}

		@Override
		public Object getData() {
			return variables.get(variableName);
		}
	}
}