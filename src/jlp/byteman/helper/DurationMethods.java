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
package jlp.byteman.helper;

import java.util.HashMap;
import java.util.Locale;
import java.util.WeakHashMap;

import org.jboss.byteman.rule.Rule;

public class DurationMethods extends MyHelper {
	
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}
	protected DurationMethods(Rule rule) {
		super(rule);
		
		
			sep= getProps().getProperty("bytemanpkg.csvSep", ";");
			title="date"+sep+"fullMethodName"+sep+"durationMethods(ms)"+sep;

			
	}
	

}
