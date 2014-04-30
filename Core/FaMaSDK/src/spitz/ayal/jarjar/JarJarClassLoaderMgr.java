/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package spitz.ayal.jarjar;

import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;



public class JarJarClassLoaderMgr{
	// === Class Fields =======================================================
		private Hashtable<String,JarJarClassLoader>	classLoaderTable = null;
		
		private static JarJarClassLoaderMgr instance;
		
	// === Class Constructor ==================================================
		private JarJarClassLoaderMgr(){
			classLoaderTable = new Hashtable<String,JarJarClassLoader>();
//			try {
//				Class.forName("org.osgi.framework.BundleContext");
//				System.out.println("Remember to register JarJarClassLoader handler!");
//			} catch (ClassNotFoundException e1) {
				URL.setURLStreamHandlerFactory(new Handler());
//			}			
		}
		
		public static synchronized JarJarClassLoaderMgr getInstance(){
			if (instance == null){
				instance = new JarJarClassLoaderMgr();
			}
			return instance;
		}
	
	// === Classs Methods =====================================================
		protected void registerClassLoader(JarJarClassLoader loader,String src){
			classLoaderTable.put(src, loader);
		}
				
		protected InputStream getResource(URL url){
			String				resStr = null;
			String				src = null;
			String				rsrcName = null;
			JarJarClassLoader	classLoader = null;
			
			String urlStr = url.toString();
			int slashIndex = urlStr.indexOf('/');
			if (urlStr.charAt(slashIndex+1)=='/')
				slashIndex = slashIndex + 2;
			
			// resStr = url.getPath(); doesn't work correctly in Windows
			// as it removes drive letter 
			
			resStr = urlStr.substring(slashIndex);
			int pathIndex = resStr.lastIndexOf('!');
			src = resStr.substring(0, pathIndex);
			rsrcName = resStr.substring(pathIndex + 1);
					
			classLoader = classLoaderTable.get(src);
			if (classLoader != null){
				return classLoader.findResourceAsStream(rsrcName);
			}
			
			return null;
		}
	}