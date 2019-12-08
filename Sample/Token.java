public class Token {
	private String name;
	private String text = "";
	private Object object = null;

	public Token(String name, String text){
		this.text = text;
		this.name = name;
	}

	public Token(){
		name = "NONE";
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}
	
	public Object getObject() {
		return object;
	}
 
	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return name + " : " + text;
	}
}
