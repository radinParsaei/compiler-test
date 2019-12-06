package Sample;

public class Main extends CompilerMain {
	public static void main(String [] args) {
        ArgParser argParser = new ArgParser(args);
        if (argParser.getItems().contains("lex")) {
            System.out.println(lex(new Sample(argParser.getArgs().get(0))));
        } else {
            try {
                compile(new Sample(argParser.getArgs().get(0)));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please enter file name");
                System.exit(1);
            }
        }
	}
}
