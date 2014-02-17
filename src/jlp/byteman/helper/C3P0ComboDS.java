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

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.jboss.byteman.rule.Rule;

public class C3P0ComboDS extends MyHelper {

	public static WeakHashMap<Object, String> hmDsName = new WeakHashMap<Object, String>();
	public static ArrayList<Object> dsDetected = new ArrayList<Object>();

	private static int countDS = 0;

	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}

	protected C3P0ComboDS(Rule rule) {
		super(rule);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"ComboPooledDataSource"+sep+"url"+sep+"IdleConnections(unit)"+sep+"BusyConnections(unit)"+sep+"maxConn(unit)"+sep+"InitialSize(unit)"+sep+"maxPreparedStatementsPerConn(unit)"+sep+"MaxPreparedStatement(unit)"+sep+"countMeasure(unit)"+sep;
		// TODO Auto-generated constructor stub
	}

	public void fillHmDs(Object ds) {

		synchronized (C3P0ComboDS.class) {

			
			dsDetected.add(ds);

			hmDsName.put(ds, "DS_" + Integer.toString(countDS));
			System.out
					.println("[Byteman Instrumentation C3P0PoolManagement] DS_"
							+ Integer.toString(countDS) + " intercepted ");
			countDS++;

		

		}
	}

	public String dbcpPoolToTrace(Object ds, String currentDate,
			int countMeasure, int frequenceMeasure) {
		// System.out.println("Rentree dans before");

		// System.out.println("ValidationConnection entree freqlogs = "+
		// freqLogs);
		synchronized (C3P0ComboDS.class) {
			if (!dsDetected.contains(ds))
				fillHmDs(ds);
			StringBuilder retour = new StringBuilder();

			String nameDS = hmDsName.get(ds);

			try {

				int idle = (Integer) ds
						.getClass()
						.getMethod("getNumIdleConnectionsDefaultUser",
								(Class[]) null).invoke(ds, (Object[]) null);

				int active = (Integer) ds
						.getClass()
						.getMethod("getNumBusyConnectionsDefaultUser",
								(Class[]) null).invoke(ds, (Object[]) null);

				// depend du pool object si pas troouve on met -1
				int maxConn = (Integer) ds.getClass()
						.getMethod("getMaxPoolSize", (Class[]) null)
						.invoke(ds, (Object[]) null);

				int initial = (Integer) ds.getClass()
						.getMethod("getInitialPoolSize", (Class[]) null)
						.invoke(ds, (Object[]) null);

				int maxPreparedStatementsPerConn = (Integer) ds
						.getClass()
						.getMethod("getMaxStatementsPerConnection",
								(Class[]) null).invoke(ds, (Object[]) null);

				String url = (String) ds.getClass()
						.getMethod("getJdbcUrl", (Class[]) null)
						.invoke(ds, (Object[]) null);

				int maxPreparedStatement = (Integer) ds.getClass()
						.getMethod("getMaxStatements", (Class[]) null)
						.invoke(ds, (Object[]) null);
				String shortNameObject = ds.getClass().getSimpleName();

				// System.out.println("idle = "+idle+" ; active = "+active);
				retour.append(currentDate).append(sep)
						.append(shortNameObject + "_" + nameDS).append(sep)
						.append(url).append(sep).append(Integer.toString(idle))
						.append(sep).append(Integer.toString(active))
						.append(sep).append(Integer.toString(maxConn))
						.append(sep).append(Integer.toString(initial))
						.append(sep)
						.append(Integer.toString(maxPreparedStatementsPerConn))
						.append(sep)
						.append(Integer.toString(maxPreparedStatement))
						.append(sep).append(countMeasure).append(sep + "\n");
				if (countMeasure >= Integer.MAX_VALUE - frequenceMeasure - 1)
					readCounter(ds, true);

			} catch (IllegalArgumentException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (SecurityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (InvocationTargetException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NoSuchMethodException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (retour.toString().length() == 0) {
				return currentDate + sep+sep+sep+sep+sep+sep+sep+sep+sep+sep;

			} else {
				String strRet = retour.toString();
				int idx = strRet.lastIndexOf('\n');
				return strRet.substring(0, idx);
			}

		}
	}

	

}
