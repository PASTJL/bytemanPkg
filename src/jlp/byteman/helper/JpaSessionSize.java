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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import jlp.byteman.helper.sizeof.RamUsageEstimator;



import org.jboss.byteman.rule.Rule;

public class JpaSessionSize extends MyHelper {

	private static DecimalFormat df = new DecimalFormat("#0.000",
			new DecimalFormatSymbols(Locale.ENGLISH));

	
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}
	

	public static double sizeMax = 0;
	public static  double sizeMin = Double.MAX_VALUE;
	public static  double sizeMoy = 0;
	

	protected JpaSessionSize(Rule rule) {
		super(rule);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"ClassName"+sep+"CurrentJpaSize(octet)"+sep+"AvgJpaSize(octet)"+sep+"MinJpaSize(octet)"+sep+"MaxJpaSize(octet)"+sep+"nbAnalysedSessions(unit)"+sep+"nbTotalSessions(unit)"+sep;
		
	}


	public String sizeJpaSessionToTrace(Object sess, boolean boolSerialization,
			String classMethod, String strategy,int frequenceMeasure, int count) {
		// how size is computed, add to class.name.method in trace file
		String prefix=strategy+"_";
		
		
		double sizeObject = 0;
		if (boolSerialization) {
			// on surcharge prefix par "serial_"
			prefix="serialized_";
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024*100);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				
					

			// On serialise

			try {
				// Sic'est un objet Session Hibernate on prend le
				// serializer de Hibernate

				if (sess.getClass().getCanonicalName()
						.equals("org.hibernate.impl.SessionImpl")) {
					try {
						
						Method meth = sess.getClass()
								.getDeclaredMethod("writeObject",
										java.io.ObjectOutputStream.class);
						meth.setAccessible(true);
						meth.invoke(sess, oos);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block

					} catch (SecurityException e) {
						// TODO Auto-generated catch block

					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block

					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block

					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block

					}
				} else {
					oos.writeObject((Serializable) sess);
				}
				oos.flush();
				sizeObject = baos.size();
				oos.close();
				baos.close();
				/*
				 * System.out.println("serialisation reussie pour objet : " +
				 * obj3.getClass().getName());
				 */

			} catch (IOException e) {
				// // TODO Auto-generated catch block
				// System.out
				// .println("erreur serialisation pour objet : "
				// + obj.getClass().getName());
				// e.printStackTrace();
				sizeObject = baos.size();
			}

			finally {
				if (null != baos) {

					try {
						baos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if (null != oos) {
					try {
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// e.printStackTrace();
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
		if (sizeObject > 0) {
			// on remplit les compteurs depuis le mbean
			if (sizeObject > sizeMax) {
				sizeMax = sizeObject;
			}
			if (sizeObject < sizeMin) {
				sizeMin = sizeObject;
			}
			int sessionExamined=count/frequenceMeasure;
			sizeMoy = (sizeMoy * sessionExamined + sizeObject)
					/ (sessionExamined + 1);

			

			// ecriture modification du mbean
			return new StringBuilder().append(prefix).append(classMethod).append(sep)
			.append(df.format(sizeObject)).append(sep)
					.append(df.format(sizeMoy)).append(sep)
					.append(df.format(sizeMin)).append(sep)
					.append(df.format(sizeMax)).append(sep)
					.append(sessionExamined).append(sep).append(count)
					.append(sep).toString();
		} else {
			return sep+sep+sep+sep+sep+sep+sep;
		}

	}

}
