import java.util.HashMap;
import java.util.ArrayList;

public class SyntaxTree {
	private static SyntaxTreeBase dataToSyntaxTreeObject(Object data) {
		if (data instanceof java.lang.Number) {
			return new Number((java.lang.Number)data);
		}
		return new Text(data.toString());
	}
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
			String value = data;
			if (value.startsWith("\"") || value.startsWith("\'")) {
				value = value.substring(1, value.length());
			}
			if (value.endsWith("\"") || value.endsWith("\'")) {
				value = value.substring(0, value.length() - 1);
			}
			this.setData(value);
			this.setType("Text");
		}
	}
	
	public static class Plus extends SyntaxTreeBase {
		private SyntaxTreeBase value1;
		private SyntaxTreeBase value2;
		public Plus (SyntaxTreeBase value1, SyntaxTreeBase value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public Object getData() {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() + ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.data == ((Double)this.data).intValue()){
					this.setData(((Double)this.data).intValue());
				}
			} else {
				this.setData(value1.toString() + value2.toString());
			}
			return this.data;
		}

		@Override
		public String getType() {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setType("Number");
			} else {
				this.setType("Text");
			}
			return this.type;
		}
	}

	public static class Minus extends SyntaxTreeBase {
		private SyntaxTreeBase value1;
		private SyntaxTreeBase value2;
		public Minus (SyntaxTreeBase value1, SyntaxTreeBase value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public Object getData() {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() - ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.data == ((Double)this.data).intValue()){
					this.setData(((Double) this.data).intValue());
				}
			} else {
				this.setData(value1.toString().replace(value2.toString(), ""));
			}
			return this.data;
		}

		@Override
		public String getType() {
			if(value1.getType() == "Number" && value2.getType() == "Number") {
				this.setType("Number");
			} else {
				this.setType("Text");
			}
			return this.type;
		}
	}
	
	public static class Multiply extends SyntaxTreeBase {
		private SyntaxTreeBase value1;
		private SyntaxTreeBase value2;

		private String stringMultiply(String string, int times) {
			String result = "";
			for(int i = 0; i < times; i++) {
				result += string;
			}
			return result;
		}

		public Multiply(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public Object getData() {
			if(value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setData(((java.lang.Number)value1.getData()).doubleValue() * ((java.lang.Number)value2.getData()).doubleValue());
				if((Double)this.data == ((Double)this.data).intValue()){
					this.setData(((Double) this.data).intValue());
				}
			} else if((value1.getType().equals("Number") && value2.getType().equals("Text"))){
				Double count;
				count = ((java.lang.Number)value1.getData()).doubleValue();
				this.setData(this.stringMultiply(value2.getData().toString(), count.intValue()));
			} else if((value1.getType().equals("Text") && value2.getType().equals("Number"))){
				Double count;
				count = ((java.lang.Number)value2.getData()).doubleValue();
				this.setData(this.stringMultiply(value1.getData().toString(), count.intValue()));
			} else {
				Sample.syntaxError("Can not calculate TEXT MULTIPLY TEXT");
				this.setData("None");
			}
			return this.data;
		}

		@Override
		public String getType() {
			if(value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setType("Number");
			} else if((value1.getType().equals("Number") && value2.getType().equals("Text"))){
				this.setType("Text");
			} else if((value1.getType().equals("Text") && value2.getType().equals("Number"))){
				this.setType("Text");
			} else {
				this.setType("Text");
			}
			return this.type;
		}
	}

	public static class Division extends SyntaxTreeBase {
		private SyntaxTreeBase value1;
		private SyntaxTreeBase value2;

		public Division(SyntaxTreeBase value1, SyntaxTreeBase value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public Object getData() {
			if (value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setData(((java.lang.Number) value1.getData()).doubleValue() / ((java.lang.Number) value2.getData()).doubleValue());
				if ((Double) this.data == ((Double) this.data).intValue()) {
					this.setData(((Double) this.data).intValue());
				}
				this.setType("Number");
			} else {
				Sample.syntaxError("Can not calculate TEXT DIVISION TEXT");
				this.setType("Text");
				this.setData("None");
			}
			return this.data;
		}

		@Override
		public String getType() {
			if (value1.getType().equals("Number") && value2.getType().equals("Number")) {
				this.setType("Number");
			} else {
				this.setType("Text");
			}
			return this.type;
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
			SyntaxTreeBase data = variables.get(variableName);
			if (data != null) {
				return data.getData();
			}
			System.err.println("variable " + variableName + " is not defined");
			return new Text("None");
		}

		@Override
		public String getType() {
			SyntaxTreeBase data = variables.get(variableName);
			if (data != null) {
				return data.getType();
			}
			System.err.println("variable " + variableName + " is not defined");
			return "Text";
		}
	}

	public static class Repeat extends SyntaxTreeBase {
		public Repeat(int times, SyntaxTreeBase toRun) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					for(int i = 0; i < times; i++){
						toRun.getRunnable().run();
					}
				}
			});
		}
	}	

	public static class Print extends SyntaxTreeBase {
		public Print(SyntaxTreeBase data, String end) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					System.out.print(data + end);
				}
			});
		}
	}

	public static class SetVariable extends SyntaxTreeBase {
		public SetVariable(String name, SyntaxTreeBase value, HashMap variables) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					variables.put(name, dataToSyntaxTreeObject(value.getData()));
				}
			});
		}
	}

	public static class SetFunction extends SyntaxTreeBase {
		public SetFunction(String name, SyntaxTreeBase value, HashMap functions) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					functions.put(name, value);
				}
			});
		}
	}

	public static class CallFunction extends SyntaxTreeBase {
		public CallFunction(String name, HashMap functions) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					try {
						((SyntaxTreeBase)functions.get(name)).getRunnable().run();
					} catch (NullPointerException e) {
						System.err.println("Function " + name + " is not defined");
					}
				}
			});
		}
	}

	public static class Programs extends SyntaxTreeBase {
		public Programs(SyntaxTreeBase runnable1, SyntaxTreeBase runnable2) {
			setRunnable(new Runnable(){
				@Override
				public void run() {
					runnable1.getRunnable().run();
					runnable2.getRunnable().run();
				}
			});
		}
	}
}