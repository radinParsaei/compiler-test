import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Parser {
	public interface CompilerLambda {
		Object run(Parser tokens);
	}
	private final ArrayList<Token> tokens;
	private boolean singleRunPerLocation = true, singleRun = false, saveTexts = false;

	public void setSingleRunPerLocation(boolean singleRunPerLocation) {
		this.singleRunPerLocation = singleRunPerLocation;
	}

	public void setSingleRun(boolean singleRun) {
		this.singleRun = singleRun;
	}

	public void setSaveTexts(boolean saveTexts) {
		this.saveTexts = saveTexts;
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
		String map = this.getMap() + " ";
		String[] models = model.split("\\|");
		int index = 0;
		for (int i = 0; i < models.length; i++) {
			models[i] = models[i].trim() + " ";
			index = map.indexOf(models[i]);
			if (index != -1) {
				model = models[i];
				break;
			}
		}
		if (index < 0) {
			return;
		}
		int listIndex = 0;
		for(int i = 0; i < index; i++) {
			if(map.charAt(i) == ' ') {
				listIndex++;
			}
		}
		StringBuilder text = null;
		if (saveTexts) text = new StringBuilder();
		int tmp = listIndex;
		ArrayList<Token> tmpTokens = new ArrayList<>();
		for(int i = 0; i < model.split(" ").length; i++) {
			tmpTokens.add(tokens.get(listIndex + i));
			if (saveTexts) text.append(tokens.get(listIndex + i).getText());
			tokens.remove(listIndex + i);
			listIndex--;
		}
		listIndex = tmp;
		Token t = new Token(newName, saveTexts? text.toString():null);
		t.setObject(lambda.run(new Parser(tmpTokens)));
		tokens.add(listIndex, t);
		if (!singleRun) {
			if (singleRunPerLocation) {
				for (String model2 : models) {
					if (map.indexOf(model2, index + 1) != -1) {
						this.on(model, newName, lambda);
					}
				}
			} else {
				for (String model2 : models) {
					if (map.contains(model2)) {
						this.on(model, newName, lambda);
					}
				}
			}
		}
	}

	public void on(String model, String newName, Object parentOfMethod, String methodName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {//replace previous model with newName and store method output on it Object
		String map = this.getMap() + " ";
		String[] models = model.split("\\|");
		int index = 0;
		for (int i = 0; i < models.length; i++) {
			models[i] = models[i].trim() + " ";
			index = map.indexOf(models[i]);
			if (index != -1) {
				model = models[i];
				break;
			}
		}
		if (index < 0) {
			return;
		}
		int listIndex = 0;
		for(int i = 0; i < index; i++) {
			if(map.charAt(i) == ' ') {
				listIndex++;
			}
		}
		StringBuilder text = null;
		if (saveTexts) text = new StringBuilder();
		int tmp = listIndex;
		ArrayList<Token> tmpTokens = new ArrayList<>();
		for(int i = 0; i < model.split(" ").length; i++) {
			tmpTokens.add(tokens.get(listIndex + i));
			if (saveTexts) text.append(tokens.get(listIndex + i).getText());
			tokens.remove(listIndex + i);
			listIndex--;
		}
		listIndex = tmp;
		Token t = new Token(newName, saveTexts? text.toString():null);
		t.setObject(parentOfMethod.getClass().getMethod(methodName, Parser.class).invoke(parentOfMethod, new Parser(tmpTokens)));
		tokens.add(listIndex, t);
		if (!singleRun) {
			if (singleRunPerLocation) {
				for (String model2 : models) {
					if (map.indexOf(model2, index + 1) != -1) {
						this.on(model, newName, parentOfMethod, methodName);
					}
				}
			} else {
				for (String model2 : models) {
					if (map.contains(model2)) {
						this.on(model, newName, parentOfMethod, methodName);
					}
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
