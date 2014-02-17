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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.jboss.byteman.rule.Rule;

public class DbcpPoolManagementPoolingDS extends MyHelper {

	public static WeakHashMap<Object,String> hmDS=new WeakHashMap<Object,String>();
	public static WeakHashMap<Object,Object> hmPool=new WeakHashMap<Object,Object>();
	private static  int countDS = 0;
	

	private String sep = getProps().getProperty("bytemanpkg.csvSep", ";");
	
	private static String title;

	public  String getTitle() {
		return title;
	}

	protected DbcpPoolManagementPoolingDS(Rule rule) {
		super(rule);
		title="date"+sep+"DataSource"+sep+"url"+sep+"IdleConnections(unit)"+sep+"BusyConnections(unit)"+sep+"maxConn(unit)"+sep+"InitialSize(unit)"+"MaxIdle(unit)"+sep+"MaxPreparedStatement(unit)"+sep+"countMeasure(unit)"+sep;
	}

	
	public void fillHmDs(Object ds, Object pool) {

		synchronized (DbcpPoolManagementPoolingDS.class) {
			if(null == pool){
				System.out.println("DbcpPoolManagementPoolingDS.<init>(pool) => pool is null wait for DbcpPoolManagementPoolingDS.setPool(pool)");
				return;
			}
			if(hmDS.containsKey(ds)) return;
			hmDS.put(ds,"DS_" + Integer.toString(countDS));
			hmPool.put(ds,pool);
			
				
				System.out
						.println("[Byteman Instrumentation DbcpPoolManagement] DS_"
								+ Integer.toString(countDS) + " interceptd ");
				countDS++;
			
			

		}
	}

	public String dbcpPoolToTrace(Object ds, String currentDate, Object obj,int countMeasure,int frequenceMeasure) {
		// System.out.println("Rentree dans before");

		// System.out.println("ValidationConnection entree freqlogs = "+
		// freqLogs);
		StringBuilder retour = new StringBuilder();
		if (!hmPool.containsKey(ds)){
			// Attach by attach API
			//
			try {
				Field _pool=ds.getClass().getDeclaredField("_pool");
				_pool.setAccessible(true);
				Object pool= _pool.get(ds);
				
				fillHmDs(ds,pool);
			} catch (NoSuchFieldException e){
				e.printStackTrace();
				
			}
			catch( SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		Object pool=hmPool.get(ds);
		
			try {

				

				int idle = (Integer) pool.getClass()
						.getMethod("getNumIdle", (Class[]) null)
						.invoke(pool, (Object[]) null);

				int active = (Integer) pool.getClass()
						.getMethod("getNumActive", (Class[]) null)
						.invoke(pool, (Object[]) null);

				// depend du pool object si pas troouve on met -1
				int maxConn = (Integer) pool.getClass()
						.getMethod("getMaxActive", (Class[]) null)
						.invoke(pool, (Object[]) null);

				int initial = (Integer) pool.getClass()
						.getMethod("getMinIdle", (Class[]) null)
						.invoke(pool, (Object[]) null);

				int maxIdle = (Integer) pool.getClass()
						.getMethod("getMaxIdle", (Class[]) null)
						.invoke(pool, (Object[]) null);

				
				String url = "null";
				// if (declaredMethods.contains("getUrl"))
				
					Constructor<?>[] constructors=obj.getClass().getDeclaredConstructors();
					for (Constructor constructor: constructors){
					
						System.out.println("constructor => "+constructor.toGenericString());
						constructor.setAccessible(true);
					
					}
					Method meth=obj.getClass().getMethod("getMetaData", (Class[]) null);
					meth.setAccessible(true);
					
					Object objTmp= meth
							.invoke(obj, (Object[]) null);
					
//					DatabaseMetaData meta = (DatabaseMetaData) objTmp.getClass().getMethod("getInnermostDelegate", (Class[]) null)
//							.invoke(objTmp, (Object[]) null);
//					
					url = (String) objTmp.getClass()
							.getMethod("getURL", (Class[]) null)
							.invoke(objTmp, (Object[]) null);

					
				
					int maxPreparedStatement =  (Integer) objTmp.getClass()
								.getMethod("getMaxStatements",
										(Class[]) null)
								.invoke(objTmp, (Object[]) null);
					String nameDS = hmDS.get(ds);
					String shortNameObject = ds.getClass().getSimpleName();
// System.out.println("idle = "+idle+" ; active = "+active);
				retour.append(currentDate).append(sep).append(shortNameObject+"_"+nameDS)
						.append(sep).append(url)
						.append(sep).append(Integer.toString(idle)).append(sep)
						.append(Integer.toString(active)).append(sep)
						.append(Integer.toString(maxConn)).append(sep)
						.append(Integer.toString(initial)).append(sep)
						.append(Integer.toString(maxIdle)).append(sep)
						.append(Integer.toString(maxPreparedStatement))
						.append(sep).append(countMeasure).append(sep+"\n");
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
			return strRet.substring(0, idx );
		}

	}

	
}
