/*Copyright 2013 Jean-Louis PASTUREL 
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/

package jlp.byteman.helper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class BytemanProperties {
	public static Properties bytemanProperties;
	// Properties are in the fine pointed by le value : prop:org.jboss.byteman.properties=/tmp/fileproperties.properties
	static {
		System.out.println("Chargement fichier properties ");
		
		Locale.setDefault(Locale.ENGLISH);

			try {
				bytemanProperties = new Properties();
				bytemanProperties.load(new FileInputStream(new File(System
						.getProperty("org.jboss.byteman.properties"))));
				System.out
						.println("Fichier aspectsPerfProperties = "
								+ (System.getProperty("rootAspectsPerf")
										+ File.separator + "META-INF"
										+ File.separator + "aspectsPerf.properties"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("The file : "
						+ System.getProperty("org.jboss.byteman.properties") + " doesn't exist");
				e.printStackTrace();

			} catch (IOException e) {
				System.out.println("The file : "
						+ System.getProperty("org.jboss.byteman.properties") + " doesn't exist");
				e.printStackTrace();
			}
		}
		

	
}
