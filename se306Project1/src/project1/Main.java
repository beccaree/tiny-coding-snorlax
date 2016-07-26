package project1;

public class Main {
	
	private static String inputFileName;
	private static int numProcessors;
	private static boolean isParallel = false;
	private static int numThreads;
	private static boolean isVisualised = false;
	private static String outputFileName = "INPUT-output.dot";

	public static void main(String[] args) {
		parseArgs(args);
	}
	
	private static void parseArgs(String[] args) {
		
		try {
			inputFileName = args[0];
			numProcessors = Integer.parseInt(args[1]);
		
			for(int i=2; i < args.length; i++) {
				if(args[i].equals("-p")) {
					isParallel = true;
					numThreads = Integer.parseInt(args[i + 1]);
				} else if (args[i].equals("-v")) {
					isVisualised = true;
				} else if (args[i].equals("-o")) {
					outputFileName = args[i + 1];
				}
			}
		} catch(Exception e) {
			System.out.println("Invalid command!");
		}
	}

}
