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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;



public class Handler implements URLStreamHandlerFactory{
	
	public URLStreamHandler createURLStreamHandler(String protocol){
		URLStreamHandler		handler = null;
		
		if (protocol.equals("jarjar")){
			handler = new JarJarURLStreamHandler();
		}

		return handler;
	}		
	
	private class JarJarURLStreamHandler extends URLStreamHandler{	
		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return new JarJarURLConnection(url);
		}
	}

	private class JarJarURLConnection extends URLConnection{
		public JarJarURLConnection(URL url){ super(url); }
		
		@Override
		public void connect() throws IOException{}

		@Override
		public InputStream getInputStream() throws IOException{
			return JarJarClassLoaderMgr.getInstance().getResource(url);
		}
	}
}

