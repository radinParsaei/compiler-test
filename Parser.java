import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	public void exitCheckLoop() {
		if (parent != null) {
			parent.isChangedSingleRun = true;
			parent.setSingleRun(true);
		}
	}

	private Parser parent = null;
	private int listIndex = 0;
	private int nullResCount = 0;
	private int from = 0;

	public int getListIndex() {
		return listIndex;
	}

	public interface CompilerLambda {
		Object run(Parser tokens);
	}
	private final ArrayList<Token> tokens;
	private boolean singleRunPerLocation = true, singleRun = false, saveTexts = false, isChangedSingleRun = false;

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
		if (isChangedSingleRun) {
			isChangedSingleRun = false;
			setSingleRun(false);
		}
		String map = this.getMap() + " ";
		Pattern	pattern = Pattern.compile(model + " ");
		Matcher matcher = pattern.matcher(map);
		for (int i = 0; i < nullResCount + 1; i++) {
			if (!matcher.find()) {
				return ;
			}
		}
		String matched = matcher.group(0);
		int index;
		if (nullResCount != 0) index = map.indexOf(matched, from);
		else index = map.indexOf(matched);
		listIndex = 0;
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
		tokens.add(listIndex, t);
		Parser parser = new Parser(tmpTokens);
		parser.parent = this;
		t.setObject(lambda.run(parser));
		if (t.getObject() == null) {
			nullResCount++;
			tokens.remove(listIndex);
			int i = 0;
			for (Token token : parser.getTokens()) {
				tokens.add(listIndex + i++, token);
			}
		}
		if (!singleRun) {
			if (singleRunPerLocation) {
				try {
					from = index + matched.length() - (matched.length() - newName.length());
					String map1 = map.substring(from);
					Matcher matcher1 = pattern.matcher(map1);
					if (matcher1.find()) {
						this.on(model, newName, lambda);
					}
				} catch (StringIndexOutOfBoundsException ignored) {}
			} else {
				this.on(model, newName, lambda);
			}
		}
		nullResCount = 0;
		from = 0;
	}

	public Parser getParent() {
		return parent;
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
