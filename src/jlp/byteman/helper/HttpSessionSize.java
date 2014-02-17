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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import jlp.byteman.helper.sizeof.RamUsageEstimator;
import jlp.byteman.helper.utils.Counters;

import org.jboss.byteman.rule.Rule;

public class HttpSessionSize extends MyHelper {
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}

	protected HttpSessionSize(Rule rule) {
		super(rule);
		Locale.setDefault(Locale.ENGLISH);

		counters = new Counters(3);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"fullMethodName"+sep+"appName"+sep+"idSession"+sep+"nbObjects(nb)"+sep+"SizeCurrent in octets(octets)"+sep+"SizeMax in Octets(octets)"+sep+"Examined(unit)"+sep;
	}

	

	 static Counters counters;
	public  long freqCount = 0;
	public boolean boolWoven = false;
	


	

	// Counter 1 => moyenne size
	// Counter 2 => maximum size
	// Counter 3 => session examined
	private static DecimalFormat df = new DecimalFormat("#0.000",
			new DecimalFormatSymbols(Locale.ENGLISH));

	private static HashMap<String, Long> hmCounter = new HashMap<String, Long>();

	

	public String toTrace(Object hev, String methodName,boolean boolSerialization, String strategy,int count) {

		// how size is computed, add to class.name.method in trace file
		String prefix=strategy+"_";
		
		Object sess = null;
		int nbObjects = 0;
		String name = "nameEmpty";
		String sessionId = "unknown";

		try {

			sess = hev.getClass().getMethod("getSession", (Class[]) null)
					.invoke(hev, (Object[]) null);
			Object obj2 = sess.getClass()
					.getMethod("getServletContext", (Class[]) null)
					.invoke(sess, (Object[]) null);
			name = ((String) obj2.getClass()
					.getMethod("getContextPath", (Class[]) null)
					.invoke(obj2, (Object[]) null)).replaceAll("\\s+", "")
					.replaceAll("/", "_");
			if (null != sess.getClass().getMethod("getId", (Class[]) null)
					.invoke(sess, (Object[]) null)) {
				sessionId = (String) sess.getClass()
						.getMethod("getId", (Class[]) null)
						.invoke(sess, (Object[]) null);
			}

		} catch (IllegalArgumentException e2) {
			System.out
					.println("  methods passage 1 IllegalArgumentException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());

			return sep+sep+sep+sep;

		} catch (SecurityException e2) {
			System.out
					.println(" methods passage 1 SecurityException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		} catch (IllegalAccessException e2) {
			System.out
					.println(" methods passage 1 IllegalAccessException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		} catch (InvocationTargetException e2) {
			System.out
					.println(" methods passage 1 InvocationTargetException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			System.out
					.println("methods passage 2  InvocationTargetException hev class ="
							+ hev.getClass().getCanonicalName());
			return sep+sep+sep+sep;
		} catch (NoSuchMethodException e2) {
			System.out
					.println(" methods passage 1 NoSuchMethodException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		}

		hmCounter.put(name, (Long) 0L);

		freqCount = (int) (long) hmCounter.get(name);
		hmCounter.put(name, (Long) ((long) (int) freqCount) + 1);

		try {

			Enumeration<String> en = (Enumeration<String>) sess.getClass()
					.getMethod("getAttributeNames", (Class[]) null)
					.invoke(sess, (Object[]) null);
			while (en.hasMoreElements()) {
				nbObjects++;
				en.nextElement();

			}
		} catch (IllegalArgumentException e2) {
			System.out
					.println(" methods passage 2 IllegalArgumentException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		} catch (SecurityException e2) {
			System.out
					.println("methods passage 2 SecurityException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		} catch (IllegalAccessException e2) {
			System.out
					.println(" methods passage 2 IllegalAccessException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		} catch (InvocationTargetException e2) {
			System.out
					.println(" methods passage 2 InvocationTargetException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			System.out
					.println("methods passage 2  InvocationTargetException sess class ="
							+ sess.getClass().getCanonicalName());
			return sep+sep+sep+sep;
		} catch (NoSuchMethodException e2) {
			System.out
					.println(" methods passage 2 NoSuchMethodException Byteman Helper => HttpSessionSize methods :"
							+ e2.getMessage());
			return sep+sep+sep+sep;
		}

		double sizeObject = 0;
		if (boolSerialization) {
			// on surcharge prefix par "serial_"
			prefix="serialized_";
			// On serialise
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				oos.writeObject(sess);
			} catch (IOException e) {
				// TODO Auto-generated catch block

			}
			/*
			 * System.out.println("serialisation reussie pour objet : " +
			 * obj3.getClass().getName());
			 */

			try {
				oos.flush();
				sizeObject = (double) baos.size();
				oos.close();
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block

			}

		} else {
			
			if ("shallowSize".equals(strategy)) {
				sizeObject = (double) getObjectSize(sess);
			} else if ("retainedHeap".equals(strategy)) {
				// retainedHeap
				sizeObject = (double) RamUsageEstimator.sizeOf(sess);
			} else {
				sizeObject = (double) getObjectSize(sess);
			}
		}
		if (!counters.getHmCount().containsKey(name)) {
			int sizeTab = counters.getSizeTab();
			Double[] tabDouble = new Double[sizeTab];
			for (int i = 0; i < sizeTab; i++) {
				tabDouble[i] = new Double(0);
			}
			counters.getHmCount().put(name, tabDouble);
		}
		// on remplit le HashMap
		Double[] tabTmpDouble = counters.getHmCount().get(name);
		tabTmpDouble[0] = sizeObject;

		if (tabTmpDouble[1] <= new Double(sizeObject)) {
			tabTmpDouble[1] = new Double(sizeObject);
		}
		tabTmpDouble[2]++;

		// sauvegarde HashMap
		counters.getHmCount().put(name, tabTmpDouble);

		// ecriture
		return (new StringBuilder().append(prefix).append(methodName).append(sep).append(name)
				.append(sep).append(sessionId).append(sep).append(nbObjects)
				.append(sep).append(df.format(tabTmpDouble[0])).append(sep)
				.append(df.format(tabTmpDouble[1])).append(sep).append(count).append(sep)).toString();

	}


}
