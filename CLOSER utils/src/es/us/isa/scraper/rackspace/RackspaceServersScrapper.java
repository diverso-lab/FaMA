package es.us.isa.scraper.rackspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class RackspaceServersScrapper {

	private Properties featsProperties;
	private Properties attProperties;

	// private Set<String> notManaged;

	public RackspaceServersScrapper(String featsProperties, String attProperties) {
		this.featsProperties = new Properties();
		this.attProperties = new Properties();
		try {
			this.featsProperties.load(new FileInputStream(featsProperties));
			this.attProperties.load(new FileInputStream(attProperties));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Collection<Configuration> parseRackspaceInstances(
			String currentGenPath, String prevGenPath) {
		Collection<Configuration> result = parseCurrentGen(currentGenPath);
//		result.addAll(parsePrevGen(prevGenPath));
		return result;
	}

	private Collection<? extends Configuration> parsePrevGen(String prevGenPath)
			throws IOException {
		// TODO Auto-generated method stub
		Collection<Configuration> result = new LinkedList<Configuration>();
		String[] arrayOS = { "Linux", "Windows", "WindowsSQLWeb",
				"WindowsSQLStd" };
		String[] nextGenArray = { "NextGen512MB", "NextGen1GB", "NextGen2GB",
				"NextGen4GB", "NextGen8GB", "NextGen15GB", "NextGen30GB" };
		String[] firstGenArray = { "FirstGen256MB", "FirstGen512MB",
				"FirstGen1GB", "FirstGen2GB", "FirstGen4GB", "FirstGen8GB",
				"FirstGen15GB", "FirstGen30GB" };
		Document doc = Jsoup.parse(new File(prevGenPath), "UTF-8", "");
		//XXX be careful with the CSS query. it may not work fine
		Elements elems = doc.select("div[id=nextgen] tbody tr[class^=main]");
		// for each tr
		int size = elems.size();
		for (int i = 0; i < size; i++) {
			Element e = elems.get(i);
			Elements tds = e.select("td");
			int tdsSize = tds.size();
			for (int j = 0; j < tdsSize; j++) {
				Element td = tds.get(j);
				String price = td.select("b").first().text();
				if (price.contains("$")) {
					// available instance
				}
			}
		}
		
		//XXX be careful with the CSS query. it may not work fine
		elems = doc.select("div[id=firstgen] tbody tr[class^=main]");
		size = elems.size();
		for (int i = 0; i < size; i++) {
			Element e = elems.get(i);
			Elements tds = e.select("td");
			int tdsSize = tds.size();
			for (int j = 0; j < tdsSize; j++) {
				Element td = tds.get(j);
				String price = td.select("b").first().text();
				if (price.contains("$")) {
					// available instance
				}
			}
		}

		return result;
	}

	private Collection<Configuration> parseCurrentGen(String currentGenPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public void extractConstraints(String input, String output) {
		try {
			FileWriter writer = new FileWriter(output);
			Document doc = Jsoup.parse(new File(input), "UTF-8", "");
			Elements elems = doc.select("div#cloud-servers");
			int size = elems.size();
			for (int i = 0; i < size; i++) {
				Element e = elems.get(i);
				Elements panes = e.select("div.panes").first().children();
				int panesSize = panes.size();
				for (int j = 0; j < panesSize; j++) {
					Element pane = panes.get(j);
					String paneId = pane.attr("id");
					String osName = paneId.substring(0, paneId.length() - 2);
					Elements instances = pane.children();
					int instancesSize = instances.size();
					for (int k = 0; k < instancesSize; k++) {
						Element instance = instances.get(k);
						if (instance.childNodeSize() > 0) {
							// we have an instance
							Element instanceNameElement = instance.select(
									"strong.normal").first();
							String instanceName = this
									.extractInstanceName(instanceNameElement
											.text());
							// String instanceName = this
							// .extractInstanceName(elementText);
							Elements li = instance.select("li");
							String ram = this.extractRAM(li.get(0).text());
							String cores = this.extractCPU(li.get(1).text());
							// the 2nd element
							String disk = this.extractDisk(li.get(3).text());
							String bandwidth = this.extractBandwidth(li.get(4)
									.text());
							Elements prices = instance
									.select(".price-actual strong");
							int nPrices = prices.size();
							String basicPrice = this.extractPrice(prices.get(0)
									.text());
							String managedPrice;
							boolean canBeManaged = true;
							if (nPrices == 1) {
								managedPrice = "N/A";
								canBeManaged = false;
							} else {
								managedPrice = this.extractPrice(prices.get(1)
										.text());

							}

							System.out.println(instanceName + " " + osName
									+ ": RAM = " + ram + ", Cores = " + cores
									+ ", Disk = " + disk + ", Bandwidth = "
									+ bandwidth + ", BasicPrice = "
									+ basicPrice + ", ManagedPrice = "
									+ managedPrice);

							String fmOS = this.featsProperties
									.getProperty(osName);
							String fmInstance = this.featsProperties
									.getProperty(instanceName);
							// String fmManaged =
							// this.featsProperties.getProperty("managed");
							// String fmRAM =
							// this.attProperties.getProperty("ram");
							// String fmCores =
							// this.attProperties.getProperty("cores");
							// String fmDisk =
							// this.attProperties.getProperty("disk");
							String fmCost = this.attProperties
									.getProperty("price");
							String finalCost = basicPrice;
							// int finalCost = this.toInteger(basicPrice);
							// String baseConstraint = fmInstance + " AND " +
							// fmOS + " IMPLIES (" +
							// fmRAM + "=" + ram + " AND " + fmCores + "=" +
							// cores +
							// " AND " + fmDisk +"="+disk+" AND " + fmCost+"=";

							String baseConstraint = fmInstance + " AND " + fmOS
									+ " IMPLIES " + fmCost + "==" + finalCost
									+ ";";
							writer.write(baseConstraint + "\n");

							// if (canBeManaged){
							// String c1 = "NOT "+fmManaged+ " " +
							// baseConstraint + basicPrice+";";
							// String c2 = fmManaged+ " " + baseConstraint +
							// managedPrice+";";
							// writer.write(c1+"\n");
							// writer.write(c2+"\n");
							// }
							// else{
							// String c = baseConstraint + basicPrice+";";
							// writer.write(c+"\n");
							// }

						}
					}
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// writer.w
	}

	private String extractInstanceName(String s) {
		// a instanceName has the following structure: "XGB"...,
		// where X is an integer number
		int firstIndex = 0;
		int secondIndex = s.indexOf(' ', firstIndex + 1);
		String result = s.substring(firstIndex, secondIndex);
		return result;
	}

	private String extractRAM(String s) {
		// TODO test this method
		String result = s.trim();
		// to obtain RAM size
		int index = result.indexOf('G');
		result = result.substring(0, index);
		return result;
	}

	private String extractCPU(String s) {
		// TODO test this method
		String result = s.trim();
		// int firstIndex = result.indexOf('"');
		int firstIndex = 0;
		int lastIndex = result.indexOf(' ');
		result = result.substring(firstIndex, lastIndex);
		return result;
	}

	private String extractDisk(String s) {
		// TODO test this method
		String result = s.trim();
		// int firstIndex = result.indexOf('"');
		int firstIndex = 0;
		int lastIndex = result.indexOf('G');
		if (lastIndex < 0) {
			result = 0 + "";
		} else {
			result = result.substring(firstIndex, lastIndex);
		}

		return result;
	}

	private String extractBandwidth(String s) {
		// TODO test this method
		String result = s.trim();
		// int firstIndex = result.indexOf('"');
		int firstIndex = 0;
		int lastIndex = result.indexOf('M');
		result = result.substring(firstIndex, lastIndex);
		return result;
	}

	private String extractPrice(String s) {
		// TODO test this method
		String result = s.trim();
		int firstIndex = result.indexOf('$') + 1;
		int lastIndex = result.indexOf('/');
		result = result.substring(firstIndex, lastIndex);
		return result;
	}

	public static void main(String... args) {
		RackspaceServersScrapper scraper = new RackspaceServersScrapper(
				"./rackspace web/properties/features.properties",
				"./rackspace web/properties/attributes.properties");
		scraper.extractConstraints("./rackspace web/rackspace servers.html",
				"./rackspace web/servers.txt");
	}

	private int toInteger(String doub) {
		double d = Double.parseDouble(doub);
		d = d * 1000;
		return (int) d;
	}
	
//	private Configuration

}
