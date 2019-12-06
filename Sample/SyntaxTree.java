package Sample;

public class SyntaxTree {
	public static class Number extends BaseClasses.SyntaxTreeBase {
		public Number(java.lang.Number data) {
			this.setData(data);
			if(((java.lang.Number) this.getData()).doubleValue() == ((java.lang.Number) this.getData()).intValue()){
				this.setData(((java.lang.Number)this.getData()).intValue());
			}
			this.setType("Number");
		}
	}
	
	public static class Text extends BaseClasses.SyntaxTreeBase {
		public Text(String data) {
			this.setData(data.substring(1, data.length() - 1));
			this.setType("Text");
		}
	}
	
	public static class Plus extends BaseClasses.SyntaxTreeBase {
		public Plus(BaseClasses.SyntaxTreeBase value1, BaseClasses.SyntaxTreeBase value2) {
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

	public static class Minus extends BaseClasses.SyntaxTreeBase {
		public Minus(BaseClasses.SyntaxTreeBase value1, BaseClasses.SyntaxTreeBase value2) {
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
	
	public static class Multiply extends BaseClasses.SyntaxTreeBase {
		private String stringMultiply(String string, int times) {
			String result = "";
			for(int i = 0; i < times; i++) {
				result += string;
			}
			return result;
		}

		public Multiply(BaseClasses.SyntaxTreeBase value1, BaseClasses.SyntaxTreeBase value2) {
			if(value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() * ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.getData() == ((Double)this.getData()).intValue()){
					this.setData(((Double) this.getData()).intValue());
				}
				this.setType("Number");
			} else if((value1.getType().equals("Number") && value2.getType().equals("Text"))){
				String tmp = "";
				double count;
				count = (Double)this.getData();
				for(int i = 0; i < count; i++) {
					tmp += value2;
				}
				this.setData(tmp);
				this.setType("Text");
			} else if((value1.getType().equals("Text") && value2.getType().equals("Number"))){
				Double count;
				count = (Double)value2.getData();
				this.setData(this.stringMultiply(value1.getData().toString(), count.intValue()));
				this.setType("Text");
			} else {
				Sample.syntaxError("Can not calculate TEXT MULTIPLY TEXT");
				this.setType("Text");
				this.setData("None");
			}
		}
	}


	public static class Division extends BaseClasses.SyntaxTreeBase {
		public Division(BaseClasses.SyntaxTreeBase value1, BaseClasses.SyntaxTreeBase value2) {
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

	public static class Result extends BaseClasses.SyntaxTreeBase {
		public Result(BaseClasses.SyntaxTreeBase value) {
			this.setData("value is " + value);
		}

		public Result(BaseClasses.SyntaxTreeBase value, long line) {
			this.setData("value is " + value + " in line " + line);
		}
	}

	public static class Print extends BaseClasses.SyntaxTreeBase {
		public Print(BaseClasses.SyntaxTreeBase value) {
			this.setData(value + "");
		}
	}

	public static class Repeat extends BaseClasses.SyntaxTreeBase {
		public Repeat(BaseClasses.SyntaxTreeBase value, BaseClasses.SyntaxTreeBase count) {
			this.setData(value + "\t" + count);
		}
	}
}