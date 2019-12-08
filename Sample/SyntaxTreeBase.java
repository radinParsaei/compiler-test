public class SyntaxTreeBase {
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
