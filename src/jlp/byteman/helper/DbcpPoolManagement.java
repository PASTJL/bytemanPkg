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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.byteman.rule.Rule;



public class DbcpPoolManagement extends MyHelper {

	public  HashMap<String,StructWeakRef> hmWeakStruc = new HashMap<String,StructWeakRef>();
	public  WeakReference<Object> tmpDsRef = null;
	private  int countDS = 0;


	private String sep= getProps().getProperty("bytemanpkg.csvSep", ";");
	
	private static String title;

	public  String getTitle() {
		return title;
	}
	public  boolean boolNativeMethod = false;
	private  int countGlobal = 0;

	public int getCountGlobal() {
		return countGlobal;
	}

	public void setCountGlobal(int countGlobal) {
		this.countGlobal = countGlobal;
	}

	protected DbcpPoolManagement(Rule rule) {
		super(rule);
		title="date"+sep+"DataSource"+sep+"url"+sep+"IdleConnections(unit)"+sep+"BusyConnections(unit)"+sep+"nbAnalysedSessions(unit)"+sep+"maxConn(unit)"+sep+"InitialSize(unit)"+sep+"MaxIdle(unit)"+sep+"MaxPreparedStatement(unit)"+sep;
	}

	public int incCountGlobal() {
		if (countGlobal > Integer.MAX_VALUE-10) countGlobal=0;
		return countGlobal;
	}
	public void fillHmDs(Object ds, String sql) {
		synchronized (DbcpPoolManagement.class) {

			tmpDsRef = new WeakReference<Object>(ds);
			Set<Entry<String, StructWeakRef>> set = hmWeakStruc.entrySet();
			Iterator<Entry<String, StructWeakRef>> it = set.iterator();
			boolean trouve = false;
			while (it.hasNext()) {
				Entry<String, StructWeakRef> entry = (Entry<String, StructWeakRef>) it.next();
				StructWeakRef swr = (StructWeakRef) entry.getValue();
				if (tmpDsRef == swr.dsRef) {
					trouve = true;
					break;
				}
			}
			if (!trouve) {
				StructWeakRef swr = new StructWeakRef("DS_"
						+ Integer.toString(countDS), tmpDsRef);
				hmWeakStruc.put("DS_" + Integer.toString(countDS), swr);
				System.out
						.println("[Byteman Instrumentation DbcpPoolManagement] DS_"
								+ Integer.toString(countDS) + " interceptd ");
				countDS++;
			}
			tmpDsRef = null;

		}
	}

	public String dbcpPoolToTrace(Object ds,String currentDate) {
		// System.out.println("Rentree dans before");

	

			// System.out.println("ValidationConnection entree freqlogs = "+
			// freqLogs);
             StringBuilder retour=new StringBuilder();
			for (int i = 0; i < countDS; i++) {
				try {

					WeakReference<Object> dsRefTmp2 = ((StructWeakRef) hmWeakStruc
							.get("DS_" + Integer.toString(i))).dsRef;

					int idle = (Integer) dsRefTmp2.get().getClass()
							.getMethod("getNumIdle", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);

					int active = (Integer) dsRefTmp2.get().getClass()
							.getMethod("getNumActive", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);
					int maxConn = (Integer) dsRefTmp2.get().getClass()
							.getMethod("getMaxActive", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);
					int initial = (Integer) dsRefTmp2.get().getClass()
							.getMethod("getInitialSize", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);
					int maxIdle = (Integer) dsRefTmp2.get().getClass()
							.getMethod("getMaxIdle", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);
					int maxPreparedStatement = (Integer) dsRefTmp2
							.get()
							.getClass()
							.getMethod("getMaxOpenPreparedStatements",
									(Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null);
					String url = "null";
					if (null != dsRefTmp2.get().getClass()
							.getMethod("getUrl", (Class[]) null)
							.invoke(dsRefTmp2.get(), (Object[]) null)) {
						url = (String) dsRefTmp2.get().getClass()
								.getMethod("getUrl", (Class[]) null)
								.invoke(dsRefTmp2.get(), (Object[]) null);
					}
					// System.out.println("idle = "+idle+" ; active = "+active);
					retour.append(currentDate).append(sep).append("DS_")
							.append(Integer.toString(i)).append(sep)
							.append(url).append(sep)
							.append(Integer.toString(idle)).append(sep)
							.append(Integer.toString(active)).append(sep)
							.append(Integer.toString(maxConn)).append(sep)
							.append(Integer.toString(maxConn)).append(sep)
							.append(Integer.toString(initial)).append(sep)
							.append(Integer.toString(maxIdle)).append(sep)
							.append(Integer.toString(maxPreparedStatement))
							.append(sep).append("\n");

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

			}
			if(retour.toString().length() == 0){
				return currentDate + sep+sep+sep+sep+sep+sep+sep+sep+sep+sep;
				
			}else
			{
			String strRet=retour.toString();
			int idx=strRet.lastIndexOf('\n');
			return strRet.substring(0, idx-1);
			}
		

	}

	class StructWeakRef {
		public String nameDS = "";
		public WeakReference<Object> dsRef = null;

		public StructWeakRef(String _nameDS, WeakReference<Object> _dsRef) {
			nameDS = _nameDS;
			dsRef = _dsRef;

		}
	}

}
