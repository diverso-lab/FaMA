package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UtilProp {
	
	private static String PROPERTIES_FILE_PATH="conf/configuration.properties";
	
	public static String getProperty(String propname) {
		
		String propertie="";
		
		try {
			Properties prop=new Properties();
			prop.load(new FileInputStream(UtilProp.PROPERTIES_FILE_PATH));
			propertie=prop.getProperty(propname);

		} catch (IOException e) {
			System.out.println("ERROR: Error while reading properties file: " + e.getMessage());
		}
		
		return propertie;
	}
	
}
