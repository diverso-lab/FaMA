package util;

import java.io.*;

public class Dot {

	
	public static void showGraph(String path, String graph){
		saveGraph(path,graph);
		saveDot(path);
		removeGraph(path);
		
	}
	
	// Save the string which represent the graph in path
	private static void saveGraph(String path, String graph) {
		
		File outputFile = null;
		outputFile = new File(path);
		FileWriter out;
		try {
			out = new FileWriter(outputFile);
			out.write(graph);
			out.close();
		}
		catch (IOException oops) {
			System.out.println("Unable to save graph to the file " + path + ".Reason: " + oops.getMessage());
		}
	}
	
	// Delete the file in path
	private static void removeGraph(String path) {
		(new File(path)).delete();
	}
	
	// Create the .png graph from the string stored in path
	private static void saveDot(String path) {
		try {
			Process p=Runtime.getRuntime().exec("dot -Tpng \"" + path + "\" -o \"" + path + ".png" + "\"");
			p.waitFor();
		} catch (IOException oops) {
			System.out.println("Unable to run DOT on " + path + ".Reason: " + oops.getMessage());
		} catch (InterruptedException oops) {
			System.out.println("DOT interrupted when processing " + path + ".Reason: " + oops.getMessage());		}
	}
	
}
