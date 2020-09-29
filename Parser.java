import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Pattern	pattern = Pattern.compile(model + " ");
		Matcher matcher = pattern.matcher(map);
		if (!matcher.find()) {
			return ;
		}
		String matched = matcher.group(0);
		int index = map.indexOf(matched);
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
		for(int i = 0; i < matched.split(" ").length; i++) {
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
				String map1 = map.substring(index);
				Matcher matcher1 = pattern.matcher(map1);
				if (matcher1.find()) {
					this.on(model, newName, lambda);
				}
			} else {
				this.on(model, newName, lambda);
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
