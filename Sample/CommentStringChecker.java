public class CommentStringChecker extends StringCheckerBase {
	@Override
	public boolean check(String data) {
		if (data.startsWith("#")) {
			return true;
		}
		return false;
	}

	@Override
	public String getText(String data) {
		return data.split("\n")[0];
	}
}