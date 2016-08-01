package project1;

public class Main {
	
	private static String inputFileName;
	private static int numProcessors;
	private static boolean isParallel = false;
	private static int numThreads;
	private static boolean isVisualised = false;
	private static String outputFileName;

	public static void main(String[] args) {
		args = new String[]{"tests/example2.dot", "1"};
		parseArgs(args);
		new GraphParser(inputFileName).parse();
		
		new GraphVisualisation();
	}
	
	private static void parseArgs(String[] args) {
		
		try {
			inputFileName = args[0];
			numProcessors = Integer.parseInt(args[1]);
			
			String output = inputFileName.substring(0, inputFileName.indexOf(".dot"));
			outputFileName = output+"-output.dot";
		
			for(int i=2; i < args.length; i++) {
				if(args[i].equals("-p")) {
					isParallel = true;
					numThreads = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-v")) {
					isVisualised = true;
				} else if (args[i].equals("-o")) {
					outputFileName = args[++i];
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid command line arguments!");
		}
	}
}
