import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Parser {
	public interface CompilerLambda {
		Object run(Parser tokens);
	}
	private ArrayList<Token> tokens;
	private boolean singleRunPerLocation = true, singleRun = false;

	public void setSingleRunPerLocation(boolean singleRunPerLocation) {
		this.singleRunPerLocation = singleRunPerLocation;
	}

	public void setSingleRun(boolean singleRun) {
		this.singleRun = singleRun;
	}
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public void remove(String token) {
		for(int i = 0; i < tokens.size(); i++) {
			if(tokens.get(i).getName().equals(token)) {
				tokens.remove(i);
				i--;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder tmp = new StringBuilder();
		for(Token t:tokens) {
			tmp.append(t).append("\n");
		}
		return tmp.toString();
	}

	public void on(String model, String newName, CompilerLambda lambda) {//replace previous model with newName and store lambda output on it Object
		String map = this.getMap();
		int index = map.indexOf(model);
		if(index < 0) {
			return;
		}
		int listIndex = 0;
		for(int i = 0; i < index; i++) {
			if(map.charAt(i) == ' ') {
				listIndex++;
			}
		}
		StringBuilder text = new StringBuilder();
		int tmp = listIndex;
		ArrayList<Token> tmpTokens = new ArrayList<>();
		for(int i = 0; i < model.split(" ").length; i++) {
			tmpTokens.add(tokens.get(listIndex + i));
			text.append(tokens.get(listIndex + i).getText());
			tokens.remove(listIndex + i);
			listIndex--;
		}
		listIndex = tmp;
		Token t = new Token(newName, text.toString());
		t.setObject(lambda.run(new Parser(tmpTokens)));
		tokens.add(listIndex, t);
		if (!singleRun) {
			if (singleRunPerLocation) {
				if (map.indexOf(model, index + 1) != -1) {
					this.on(model, newName, lambda);
				}
			} else {
				if (map.indexOf(model) != -1) {
					this.on(model, newName, lambda);
				}
			}
		}
	}

	public void on(String model, String newName, Object parentOfMethod, String methodName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {//replace previous model with newName and store method output on it Object
		String map = this.getMap();
		int index = map.indexOf(model);
		if(index < 0) {
			return;
		}
		int listIndex = 0;
		for(int i = 0; i < index; i++) {
			if(map.charAt(i) == ' ') {
				listIndex++;
			}
		}
		StringBuilder text = new StringBuilder();
		int tmp = listIndex;
		ArrayList<Token> tmpTokens = new ArrayList<>();
		for(int i = 0; i < model.split(" ").length; i++) {
			tmpTokens.add(tokens.get(listIndex + i));
			text.append(tokens.get(listIndex + i).getText());
			tokens.remove(listIndex + i);
			listIndex--;
		}
		listIndex = tmp;
		Token t = new Token(newName, text.toString());
		t.setObject(parentOfMethod.getClass().getMethod(methodName, Parser.class).invoke(parentOfMethod, new Parser(tmpTokens)));
		tokens.add(listIndex, t);
		if (!singleRun) {
			if (singleRunPerLocation) {
				if (map.indexOf(model, index + 1) != -1) {
					this.on(model, newName, parentOfMethod, methodName);
				}
			} else {
				if (map.indexOf(model) != -1) {
					this.on(model, newName, parentOfMethod, methodName);
				}
			}
		}
	}

	public String getMap() {
		StringBuilder tmp = new StringBuilder();
		for(Token token : tokens) {
			tmp.append(token.getName()).append(" ");
		}
		try {
			return tmp.substring(0, tmp.length() - 1);
		} catch(IndexOutOfBoundsException ignore) {}
		return "";
	}

	public String getTexts() {
		StringBuilder tmp = new StringBuilder();
		for(Token token : tokens) {
			tmp.append(token.getText());
		}
		return tmp.toString();
	}

	public ArrayList<Token> getTokens(){
		return tokens;
	}
}
