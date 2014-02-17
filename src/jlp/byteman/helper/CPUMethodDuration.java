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

public class CPUMethodDuration extends MyHelper {
	public static WeakHashMap<String, Long> hmDebUser = null;

	public WeakHashMap<String, Long> getHmDebUser() {
		return hmDebUser;
	}

	public boolean putInHmDebUser(String idMeth, long cpuTime){
		synchronized(CPUMethodDuration.class){
		hmDebUser.put(idMeth, new Long(cpuTime));
		}
		return true;
	}

	public long getInHmDebUser(String idMeth){
		synchronized(CPUMethodDuration.class){
			if(!hmDebUser.containsKey(idMeth)) { return -1;}
		return (long) hmDebUser.get(idMeth);
		}
		
	}
	// to avoid memory leak
	public long delInHmDebUser(String idMeth){
		synchronized(CPUMethodDuration.class){
			if(!hmDebUser.containsKey(idMeth)) { return -1;}
		return (long) hmDebUser.remove(idMeth);
		}
		
	}
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}
	protected CPUMethodDuration(Rule rule) {
		super(rule);
		
		if (null == hmDebUser) {
			hmDebUser = new WeakHashMap<String, Long>();
			sep= getProps().getProperty("bytemanpkg.csvSep", ";");
			title="date"+sep+"fullMethodName"+sep+"CPUdurationMethods(ms)"+sep+"totalDurationMethods(ms)"+sep+"percentCPU(percent)"+sep;

			Locale.setDefault(Locale.ENGLISH);
			if (tMB.isThreadCpuTimeSupported()) {
				if (!tMB.isThreadCpuTimeEnabled()) {
					tMB.setThreadCpuTimeEnabled(true);
				}
				
				System.out
						.println("CPUDurationSimpleMethods . Supports CPU Time for all Threads ");
			} else {
				System.out
						.println("CPUDurationSimpleMethods . Don t Supports CPU Time for all Threads ");
			}
		}
	}
	

}
