package es.us.isa.fama.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVExperimentWriter {

	private PrintWriter printer;
	private char separator;
//	private CSVWriter writer;
	private String fmName;
	
	public CSVExperimentWriter(String path){
		try {
//			writer = new CSVWriter(new FileWriter(path), ';');
			printer = new PrintWriter(path);
			separator = ';';
			writeHeaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFmName() {
		return fmName;
	}

	public void setFmName(String fmName) {
		this.fmName = fmName;
	}

	public void writeLine(int tenants, int prefs, int round, 
			int currentTenants, long time, double ft0, double ft1, double ftRandom, 
			int[] wi, int[] pi, boolean improves, double[] m12i, double[] m2i, double[] randomi,
			double mean12, double mean2, double meanRandom){
		
//		String[] line = {fmName, ""+tenants, ""+prefs, ""+round, ""+currentTenants,
//				""+time, ""+ft0, ""+ft1};
		String line = fmName+separator+tenants+separator+prefs+separator+round+separator
				+currentTenants+separator+time+separator+ft0+separator+ft1+separator+
				ftRandom+separator+
				mean12+separator+mean2+separator+meanRandom+separator+improves+separator;
		for (int i = 0; i < wi.length; i++){
			line += wi[i]+"-"+ pi[i]+"-"+m12i[i]+"-"+m2i[i]+"-"+randomi[i]+separator;
		}
		printStringArray(line);
//		writer.writeNext(line);
		printer.println(line);
	}
	
	private void writeHeaders(){
		String header = "FM;Tenants;Preferences per tenant;Round;Current tenants;Time;F(Ct-1);F(Ct);F(random);MeanCt-1;MeanCt;MeanCrandom;Improves;";
//		String[] header = {"FM","Tenants", "Preferences per tenant", "Round",
//				"Current tenants", "Time", "F(Ct-1)", "F(Ct)"};
		printStringArray(header);
		printer.println(header);
//		writer.writeNext(header);
	}
	
	public void finishWriting(){
		printer.flush();
		printer.close();
//			writer.flush();
//			writer.close();
	}
	
	private void printStringArray(String... strings){
		String s = "";
		for (int i = 0; i < strings.length; i++){
			s += strings[i] + " ";
		}
		System.out.println(s);
	}
}
