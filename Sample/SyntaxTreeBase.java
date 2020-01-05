public class SyntaxTreeBase {
	protected Object data;
	protected String type = "";
	protected Runnable runnable = null;

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

	protected void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public Runnable getRunnable() {
		return runnable;
	}

	@Override
	public String toString() {
		return this.getData().toString();
	}
}
