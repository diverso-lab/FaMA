package util;

import java.io.File;

public class UtilFile {

	 static public boolean deleteFiles(File path) {
		 boolean res = false;  
		 if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		           files[i].delete();
		    }
		      res = true;  
		  }
		 return res;
	 }
}
