public class Main extends CompilerMain {
	public static void main(String [] args) {
		ArgParser argParser = new ArgParser(args);
		String fileName;
		try {
			fileName = argParser.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			fileName = "";
		}
		Sample sample = new Sample(fileName, argParser.getItems().contains("shell"));
		do {
			if (argParser.getItems().contains("lex")) {
				System.out.println(lex(sample));
			} else {
				try {
					compile(sample);
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Please enter file name");
					System.exit(1);
				}
			}
		} while (argParser.getItems().contains("shell"));
	}
}
