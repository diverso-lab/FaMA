package es.us.isa.aws.scraper.ec2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class uses Template method pattern to extract cost info from Amazon EC2 page
 * @author jesus
 *
 */
public class EC2Scraper {

	private final static int INT_RESIZING = 1000;
	private boolean integerFormat;
		
	protected Properties osProp;
	protected Properties zoneProp;
	protected Properties apiNames;
	protected Properties attNames;
	
	protected Collection<Object> instancesCol;

	private String outputPath;
	private String propertiesDir;

	private Document doc;
	
	protected String dedicated;
	protected String reserved;
	
	
	public EC2Scraper(String path, String propertiesDir){
		try {
			doc = Jsoup.parse(new File(path), "UTF-8", "");
			this.propertiesDir = propertiesDir;
			loadPropertyFiles();
			integerFormat = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setIntegerFormat(boolean b){
		this.integerFormat = b;
	}
	
	public boolean isIntegerFormat(){
		return integerFormat;
	}

	private void loadPropertyFiles() {
		osProp = new Properties();
		zoneProp = new Properties();
		apiNames = new Properties();
		attNames = new Properties();
		try {
			osProp.load(new FileInputStream(new File(propertiesDir
					+ "/OS.properties")));
			zoneProp.load(new FileInputStream(new File(propertiesDir
					+ "/zones.properties")));
			apiNames.load(new FileInputStream(new File(propertiesDir
					+ "/apiName.properties")));
			attNames.load(new FileInputStream(new File(propertiesDir
					+ "/attNames.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void parseInstances(){
		System.out.println("## Instances");

		String[] areasArray = {"VA","ORE","CA","IR",
				"SIN","Tokyo","Sydney","SaoPaulo"};
		Map<String,Collection<String>> areaInstances = new HashMap<String, Collection<String>>();
		Map<String,Map<String,String>> instanceCharacteristics = new HashMap<String, Map<String,String>>();
		
		Elements reservingCategories = doc.select("div[class=parbase section tabs]");
		//for each reserving category {On Demand, Light, Medium, Heavy}
		//XXX we just take the first table, i.e. on demand
		for (int i = 0; i < 1; i++){
			Element category = reservingCategories.get(i);
			Elements oss = category.select("div[class^=pricing-table]");
			//for each OS
			//XXX just considering linux 'cause we already know
			//constraints about OSS
			for (int j = 0; j < 1; j++){
				Element os = oss.get(j);
				Elements areas = os.select("div[class^=content]");
				//for each area
				
				for (int k = 0; k < areas.size(); k++){
					Element area = areas.get(k);
					Elements instances = area.select("tr[class=sizes]");
					// and for each instance
					Collection<String> instancesCol = new ArrayList<String>();
					for (int l = 0; l < instances.size(); l++){
						Element instance = instances.get(l);
						Elements rows = instance.select("td");
						String instanceName = processInstanceId(rows.get(0).text());
						//XXX with this boolean, we exclude instances with undefined price
						boolean priceIsDefined = rows.get(5).text().contains("$");
						if (k == 0){
							//if we're processing Virginia, then obtain instance characteristics
							Map<String,String> chars = new HashMap<String, String>();
							chars.put("cores", rows.get(1).text());
							String ram = rows.get(3).text();
							if (integerFormat){
								Integer ramAux = this.toFaMaValue(ram);
								ram = ""+ramAux;
							}
							chars.put("ram", ram);
							chars.put("disk", rows.get(4).text());
							
							instanceCharacteristics.put(instanceName, chars);
						}
						if (priceIsDefined){
							instancesCol.add(instanceName);
						}
						
					}
					areaInstances.put(areasArray[k], instancesCol);
				}
			}
		}
		
		//print att Values
		String coresAtt = this.attNames.getProperty("cores");
		String diskAtt = this.attNames.getProperty("disk");
		String ramAtt = this.attNames.getProperty("ram");
		
		Set<Entry<String,Map<String,String>>> entries1 = instanceCharacteristics.entrySet();
		Collection<String> ssdInstances = new LinkedList<String>();
		Collection<String> nonSSDInstances = new LinkedList<String>();
		for (Entry<String,Map<String,String>> e:entries1){
			String iName = this.processInstances(e.getKey());
			if (iName != null){
				Map<String,String> chars = e.getValue();
				String s1 = this.processCPU(chars.get("cores"));
				String s2 = this.processRAM(chars.get("ram"));
				String s3 = this.processDisk(chars.get("disk"));
				boolean isSSD = this.isSSDDisk(chars.get("disk"));
				if (isSSD){
					ssdInstances.add(iName);
				}
				else{
					nonSSDInstances.add(iName);
				}
				
				System.out.println(iName + " IMPLIES "+ "("
						+ coresAtt+"=="+s1 + " AND " + ramAtt + "=="+s2 + " AND " + diskAtt + "=="+s3+");");
			}
//			System.out.println(e.getKey()+": "+chars.get("cores") + "vCPU, "+chars.get("ram")+" RAM, " +
//					chars.get("disk") + " GB");
		}
		
		// XXX SSD constraints
		System.out.println();
		System.out.println("### SSD constraints");
		String ssdConstraint = "SSD IFF (";
		for (String instance:ssdInstances){
			ssdConstraint += instance + " OR ";
		}
		ssdConstraint = ssdConstraint.substring(0, ssdConstraint.length() - 4) + ");";
		System.out.println(ssdConstraint);
		
		String nonSSDConstraint = "(NOT SSD) IFF (";
		for (String instance:nonSSDInstances){
			nonSSDConstraint += instance + " OR ";
		}
		nonSSDConstraint = nonSSDConstraint.substring(0, nonSSDConstraint.length() - 4) + ");";
		System.out.println(nonSSDConstraint);
		
		//print non available instances per area
		//we compare each area to Virginia, which has all the kinds of instances
		System.out.println();
		System.out.println("## Location constraints");
		Collection<String> allInstances = areaInstances.get(areasArray[0]);
		Set<Entry<String,Collection<String>>> entries2 = areaInstances.entrySet();
		for (Entry<String,Collection<String>> e:entries2){
			Collection<String> nonAvailableInstances = new ArrayList<String>(allInstances);
			nonAvailableInstances.removeAll(e.getValue());
			if (!nonAvailableInstances.isEmpty()){
				String line = e.getKey()+" IMPLIES (";
				for (String s:nonAvailableInstances){
					String s1 = this.processInstances(s);
					if (s1 != null){
						line+="NOT "+s1+" AND ";
					}
					 
				}
				line = line.substring(0, line.length() - 5);
				line+=");";
				
				System.out.println(line);
			}
		}
		
		
	}
	
	public void parsePrices(){
		System.out.println();
		System.out.println("## Pricing");
//		reservedInstances = new double[8][6][28][3][2][2];
		
		String costAtt = this.attNames.getProperty("cost");
		String upfrontCostAtt = this.attNames.getProperty("upfrontCost");
		String[] aYearArray = {"OneYearMedium", "OneYearHeavy"};
		String[] threeYearsArray = {"ThreeYearsMedium", "ThreeYearsHeavy"};
		
		boolean reserved = false;
//		String[] reservingArray = {"NOT PayInAdvance","Light","Medium","Heavy"};
		String[] ossArray = {osProp.getProperty("Linux"), osProp.getProperty("RHEL"),
				osProp.getProperty("SLES"),osProp.getProperty("Windows"),
				osProp.getProperty("SQLStandard"),osProp.getProperty("SQLWeb")};
		String[] areasArray = {"VA","ORE","CA","IR",
				"SIN","Tokyo","Sydney","SaoPaulo"};
		
		Elements reservingCategories = doc.select("div[class=parbase section tabs]");
		//for each reserving category {On Demand, Light, Medium, Heavy}
		for (int i = 0; i < reservingCategories.size(); i++){
			Element category = reservingCategories.get(i);
			Elements oss = category.select("div[class^=pricing-table]");
			if (i == 1){
				reserved = true;
			}
			//for each OS
			for (int j = 0; j < oss.size(); j++){
				Element os = oss.get(j);
				Elements areas = os.select("div[class^=content]");
				//for each area
				for (int k = 0; k < areas.size(); k++){
					Element area = areas.get(k);
					Elements instances = area.select("tr[class=sizes]");
					// and for each instance
					
					for (int l = 0; l < instances.size(); l++){
						Element instance = instances.get(l);
						Elements rows = instance.select("td");
						String instanceName = this.processInstances(rows.get(0).text());
//						int instanceIndex = getInstanceIndex(instanceName);
						if (instanceName != null){
							//it is not a micro instance.
							//we do not consider micro instances
							if (!reserved){
								String costHour = this.processPricePerHour(rows.get(5).text());
								if (costHour != null){
									if (integerFormat){
										Integer costHourAux = this.toFaMaValue(costHour);
										costHour = ""+costHourAux;
									}
									System.out.println("("+instanceName + " AND "
											+ areasArray[k] + " AND "+ossArray[j]+" AND NOT PayInAdvance)"
											+ " IMPLIES (" + costAtt+"==" +costHour+");");
								}
							}
							else{
								String fixed1 = this.processPrice(rows.get(1).text());
								if (fixed1 !=  null){
									String hour1 = this.processPricePerHour(rows.get(2).text());
									if (integerFormat){
										Integer costAux = this.toFaMaValue(fixed1);
										fixed1 = ""+costAux;
										costAux = this.toFaMaValue(hour1);
										hour1 = ""+costAux;
									}
									String s1 = "("+ instanceName + " AND " + aYearArray[i-1] +" AND "
											+ areasArray[k] +" AND "+ossArray[j]+") IMPLIES (" 
											+upfrontCostAtt+"=="+fixed1 + " AND "
											+ costAtt+"=="+hour1+");";
									System.out.println(s1);
									
									double dfixed1 = Double.parseDouble(fixed1);
									double dhour1 = Double.parseDouble(hour1);
									
//									reservedInstances[k][j][instanceIndex][i-1][0][0] = dfixed1;
//									reservedInstances[k][j][instanceIndex][i-1][0][1] = dhour1;

								}
								else{
									//we got a N/A
									String s = "("+ instanceName + " AND "
											+ areasArray[k] +" AND "+ossArray[j]+
											") IMPLIES NOT "+aYearArray[i-1]+";";
									System.out.println(s);
									
//									reservedInstances[k][j][instanceIndex][i-1][0][0] = 0;
//									reservedInstances[k][j][instanceIndex][i-1][0][1] = 0;
								}
								
								String fixed3 = this.processPrice(rows.get(3).text());
								if (fixed3 != null){
									String hour3 = this.processPricePerHour(rows.get(4).text());
									if (integerFormat){
										Integer costAux = this.toFaMaValue(fixed3);
										fixed3 = ""+costAux;
										costAux = this.toFaMaValue(hour3);
									 	hour3 = ""+costAux;
									}
									
									String s2 = "("+ instanceName + " AND " + threeYearsArray[i-1] +" AND "
											+ areasArray[k] +" AND "+ossArray[j]+") IMPLIES (" 
											+upfrontCostAtt+"=="+fixed3 + " AND "
											+ costAtt+"=="+hour3+");";
									System.out.println(s2);
									
									double dfixed3 = Double.parseDouble(fixed3);
									double dhour3 = Double.parseDouble(hour3);
									
//									reservedInstances[k][j][instanceIndex][i-1][1][0] = dfixed3;
//									reservedInstances[k][j][instanceIndex][i-1][1][1] = dhour3;
								}
								else{
									//we got a N/A
									String s = "("+ instanceName + " AND "
											+ areasArray[k] +" AND "+ossArray[j]+
											") IMPLIES NOT "+threeYearsArray[i-1]+";";
									System.out.println(s);
//									reservedInstances[k][j][instanceIndex][i-1][1][0] = 0;
//									reservedInstances[k][j][instanceIndex][i-1][1][1] = 0;
								}
							}
						}
//						System.out.println(instance);
					}
				}
			}
		}
//		try {
//			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("EC2 reserved prices"));
////			out.writeObject(reservedInstances);
//			out.flush();
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	
//	private int getInstanceIndex(String instanceName) {
//		return instances.indexOf(instanceName);
//	}

	private Integer toFaMaValue(String s){
		Double d = Double.parseDouble(s);
		return toIntegerDomain(d);
	}
	
	/**
	 * In this method, we cast double value of cost to an integer value.
	 * It's required to analyze it with FaMa
	 * @param d
	 * @return
	 */
	protected Integer toIntegerDomain(Double d){
		Double aux = d*INT_RESIZING;
		return aux.intValue();
	}
	
	private String processCPU(String s){
		return s;
	}
	
	private String processDisk(String s){
		String[] splitString = s.split(" ");
		if (splitString.length > 2){
			int i1 = Integer.parseInt(splitString[0]);
			int i2 = Integer.parseInt(splitString[2]);
			int result = i1*i2;
			return ""+result;
		}
		else{
			return splitString[0];
		}
		
	}
	
	private boolean isSSDDisk(String s){
		boolean result = false;
		if (s.contains("SSD")){
			result = true;
		}
		return result;
		
	}
	
	private String processRAM(String s){
		return s;
	}
	
	private String processInstances(String s){
		String instanceId = processInstanceId(s);
		return this.apiNames.getProperty(instanceId);
	}
	
	private String processPrice(String s){
		//XXX we remove '$' character
//		if (s.contains("N/A")){
		if (!s.contains("$")){
			return null;
		}
		else{
			return s.substring(1,s.length());
		}
		
	}
	
	private String processPricePerHour(String s){
		String[] splitString = s.split(" ");
		String result = splitString[0];
		return processPrice(result);
	}
	
	private String processInstanceId(String id){
		return excludeExtraCharacters(id);
	}
	
	private String excludeExtraCharacters(String id){
		String[] components = id.split(" ");
		return components[0];
	}
	
	public void setOutput(String path){
		try {
			File f = new File(path);
			if (!f.exists()){
				f.createNewFile();
			}
			System.setOut(new PrintStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void attributed2Basic(String path, String output){
		// easy to do it
		// every line we get with a '.'
		try {
			PrintWriter writer = new PrintWriter(output);
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = reader.readLine()) != null){
				if (!line.contains(".")){
					writer.write(line + "\n");
				}
			}
			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EC2Scraper scraper = new EC2Scraper("./ec2 pricing snapshopts/AprilEC2.html","./properties");
//		scraper.setOutput("AWS Constraints.txt");
//		scraper.setIntegerFormat(false);
//		scraper.parseInstances();
//		scraper.parsePrices();
		scraper.attributed2Basic("AmazonEC2Atts.afm", "EC2Basic.fm");
	}

}
