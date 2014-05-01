package es.us.isa.aws.scraper.ec2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EC2InstanceDetailsScraper {

	private String outputPath;
	private String propertiesDir;
	private String inputPath;
	private Properties instancesAPI;
	private Properties attNames;

	private Document doc;
	
	public EC2InstanceDetailsScraper(String propertiesDir, String outputPath, String inputPath){
		this.outputPath = outputPath;
		this.propertiesDir = propertiesDir;
		this.inputPath = inputPath;
		loadPropertyFiles();
		File input = new File(inputPath);
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPropertyFiles() {
		instancesAPI = new Properties();
		attNames = new Properties();
		try {
			instancesAPI.load(new FileInputStream(propertiesDir+"apiName.properties"));
			attNames.load(new FileInputStream(propertiesDir+"attNames.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseDetails(){
		
		File input = new File(inputPath);
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element elem = doc.select("table.Types").first();
		Elements rows = elem.select("tr.RowSelectable");
		int instances = rows.size();
		for (int i = 0; i < instances; i++){
			Element row = rows.get(i);
			Elements children = row.children();
			// api name
			Element apiName = children.get(2);
			Element auxElem = apiName.select("a").first();
			String apiNameString;
			if (auxElem == null){
				// no link
				apiNameString = apiName.select("div").first().text();
			}
			else{
				// link
				apiNameString = apiName.select("a").first().text();
			}
			
			if (!apiNameString.contains("micro")){
//				System.out.println(apiNameString);
				String fmName = instancesAPI.getProperty(apiNameString);
				// cores
				Element cores = children.get(3);
				String coresString = cores.select("div").first().text();
				String aux = coresString.substring(0,coresString.length() - 1);
				Integer coresInteger = Integer.parseInt(aux);
				
//				String cores
				// cu
				Element cus = children.get(4);
				String cusString = cus.select("div").first().text();
				aux = cusString.substring(0, cusString.length() - 1);
				// XXX multiplicamos por 10 la capacidad de computacion
				Double cusDouble = Double.parseDouble(aux) * 10;
				Integer cusInteger = cusDouble.intValue();
				// ram
				Element ram = children.get(5);
				String ramString = ram.select("div").first().text().replace(" ","");
				aux = ramString.substring(0, ramString.length() - 1);
				// XXX multiplicamos por 10 la RAM
				Double ramDouble = Double.parseDouble(aux) * 10;
				Integer ramInteger = ramDouble.intValue(); 
				// storage
//				Element storage = children.get(6);
//				String storageString = storage.select("div").first().text();
				// IO
//				Element io = children.get(7);
//				String ioString = io.select("div").first().text();
				
				String constraint = fmName + " IMPLIES ("
						+ attNames.getProperty("cores") + "==" + coresInteger + " AND "
						+ attNames.getProperty("cu") + "==" + cusInteger + " AND "
						+ attNames.getProperty("ram") + "==" + ramInteger + ");";
				System.out.println(constraint);
			}
			
		}
		
	}
	
	public static void main(String... args){
		EC2InstanceDetailsScraper scraper = new EC2InstanceDetailsScraper("properties/","instanceProperties.txt","ec2 web/ec2InstancesInfo.html");
		try {
			System.setOut(new PrintStream("instances.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		scraper.parseDetails();
	}
	
}
