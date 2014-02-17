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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

import jlp.byteman.helper.utils.Tuple2;
import jlp.byteman.helper.utils.Tuple2Method;

import org.jboss.byteman.rule.Rule;

public class GetBasicAttributesValues extends MyHelper {

	protected GetBasicAttributesValues(Rule rule) {
		super(rule);
		// TODO Auto-generated constructor stub
	}

	// Version for Java 1.5+
	public static WeakHashMap<Object, Long> hmObject = new WeakHashMap<Object, Long>();
	public static Long idObject = 0L;
	public static HashMap<String, Boolean> hmPropsFilled = null;
	private  String sep= getProps().getProperty("bytemanpkg.csvSep", ";");

	// The GenericBean contains a field currentAge wich is the date of the last
	// visit or the counter of the last visit

	// private static Map<String,jlp.byteman.helper.utils.Trace> hmOutFiles=
	// Collections.synchronizedMap(new
	// HashMap<String,jlp.byteman.helper.utils.Trace>());
	private static Map<String, String> hmOutFiles = Collections
			.synchronizedMap(new HashMap<String, String>());
	private static Map<String, Long> hmCurrentAge = Collections
			.synchronizedMap(new HashMap<String, Long>());
	private static HashMap<String, ArrayList<String>> notToTrigger = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> toTrigger = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<Tuple2>> hmInternalAttribute = new HashMap<String, ArrayList<Tuple2>>();

	private static HashMap<Class<?>, ArrayList<Method>> hmIninheritedMethods = new HashMap<Class<?>, ArrayList<Method>>();
	// External Attribute must be acceded by a method beginning by get or is.
	private static HashMap<String, ArrayList<Tuple2>> hmExternalAttribute = new HashMap<String, ArrayList<Tuple2>>();

	// hmap key = fullname of the class value CompositeType
	private static HashMap<String, CompositeType> hmClassCT = new HashMap<String, CompositeType>();
	private static HashMap<String, Integer> hmDepthIntrospect = new HashMap<String, Integer>();

	private void fillProps(Properties props, String alias) {
		hmPropsFilled.put(alias, true);

		System.out
				.println(" initialisation Byteman Helper   : GetBasicAttributesValues");

		// arrClasses = new ArrayList<String>(lst);
		String strNotToTrigger = props.getProperty("getBasicAttributesValues_"
				+ alias + ".notToTrigger", "");
		if (strNotToTrigger.trim().length() > 0) {
			ArrayList<String> locNotToTrigger = new ArrayList<String>(
					Arrays.asList(strNotToTrigger.split(";")));
			notToTrigger.put(alias, locNotToTrigger);

		}
		String strToTrigger = props.getProperty("getBasicAttributesValues_"
				+ alias + ".toTrigger", "");

		if (strToTrigger.trim().length() > 0) {
			ArrayList<String> locToTrigger = new ArrayList<String>(
					Arrays.asList(strToTrigger.split(";")));
			toTrigger.put(alias, locToTrigger);
		}

		// Liste des classes a exporter ( elles sont separes par un ; dans le
		// fichier de props)
		// The property dephtInstrospect is like
		// (<FullNameClass|"All">:<depth>";")+
		String strDepth = props.getProperty("getBasicAttributesValues_" + alias
				+ ".depthAnalysis", "-1");

		hmDepthIntrospect.put(alias, new Integer(strDepth));

	}

	private final synchronized void addClass(String nameClazz, ClassLoader cl,
			String alias) {
		Class<?> myClazz = null;
		try {
			myClazz = Class.forName(nameClazz, false, cl);

			Field[] tabFields = myClazz.getDeclaredFields();
			System.out.println("addClass : nameClass=>" + nameClazz);
			ArrayList<Tuple2> lstTup = new ArrayList<Tuple2>();
			ArrayList<String> locToTrigger = toTrigger.get(alias);
			ArrayList<String> locNotToTrigger = notToTrigger.get(alias);
			for (Field field : tabFields) {

				if (!field.isSynthetic()) {
					// On applique le filtre d include et d exclude
					boolean take0 = false;
					String name = field.getName();

					if (locToTrigger.size() == 0)
						take0 = true;
					for (String weave : locToTrigger) {

						Pattern pat = Pattern.compile(weave);
						if (pat.matcher(nameClazz + "." + name).find()) {
							take0 = true;
						}
					}
					for (String notWeave : locNotToTrigger) {
						Pattern pat = Pattern.compile(notWeave);
						if (pat.matcher(nameClazz + "." + name).find()) {
							take0 = false;
						}
					}

					if (take0) {
						// On mets dans hmInternalAttribute
						lstTup.add(new Tuple2(field.getName(), field.getType()
								.getName()));
					}
				}
			}
			hmInternalAttribute.put(nameClazz, lstTup);
			Method[] tabMethods = Class.forName(nameClazz, false, cl)
					.getDeclaredMethods();
			ArrayList<Tuple2> lstTupMeth = new ArrayList<Tuple2>();
			// trouver les methodes get et set hors getter et setter interne

			ArrayList<Tuple2> lstTupMethAlias = new ArrayList<Tuple2>();
			for (Method met : tabMethods) {
				String name = met.getName();
				if (!met.isSynthetic() && (met.getParameterTypes().length == 0)
						&& (name.startsWith("get") || name.startsWith("is"))) {
					// && ((null == arrNotWeave) || !arrNotWeave
					// .contains(clazz + "." + name))) {
					boolean take0 = false;

					if (locToTrigger.size() == 0)
						take0 = true;
					for (String weave : locToTrigger) {
						Pattern pat = Pattern.compile(weave);
						if (pat.matcher(nameClazz + "." + name).find()) {
							take0 = true;
						}
					}
					for (String notWeave : locNotToTrigger) {
						Pattern pat = Pattern.compile(notWeave);
						if (pat.matcher(nameClazz + "." + name).find()) {
							take0 = false;
						}
					}
					if (take0) {
						String str2 = null;
						if (name.startsWith("get")) {
							str2 = name.substring(3);
						} else if (name.startsWith("is")) {
							str2 = name.substring(2);
						}
						boolean take = true;
						for (Tuple2 tup : lstTup) {
							if (tup.name.toLowerCase().equals(
									str2.toLowerCase())) {
								take = false;
							}
						}

						if (take) {
							String firstLetter = str2.substring(0, 1)
									.toLowerCase();
							String nameParam = firstLetter + str2.substring(1);
							lstTupMethAlias.add(new Tuple2(nameParam, met
									.getReturnType().getName()));
							lstTupMeth.add(new Tuple2(name, met.getReturnType()
									.getName()));
						}
					}

				}
			}
			hmExternalAttribute.put(nameClazz, lstTupMeth);
			// Traitement des methodes des superClasses de profondeur
			// depthIntrospect

			// test depth =1
			List<Method> listLocIninheritedMethods = new ArrayList<Method>();
			System.out.println(" recherche get dans classes meres");
			Class<?> mySuperClazz = myClazz.getSuperclass();
			if (mySuperClazz.getName().equals("java.lang.Object")) {
				System.out.println("SuperClass is java.lang.Object ");
			} else {
				HashMap<Class<?>, Method[]> returnHm = new HashMap<Class<?>, Method[]>();

				if (hmDepthIntrospect.get(alias) >= 0) {
					listLocIninheritedMethods = getAllGetIneheritedMethods(
							mySuperClazz.getName(), new ArrayList<Method>(),
							cl, hmDepthIntrospect.get(alias), alias);
				}

				System.out
						.println("After Call getAllGetIneheritedMethods listLocIninheritedMethods nb candidate Methods :"
								+ listLocIninheritedMethods.size());
			}
			ArrayList<Tuple2> lstTupMethAliasBis = new ArrayList<Tuple2>();
			ArrayList<Method> listLocIninheritedMethodsFiltered = new ArrayList<Method>();
			System.out.println("listLocIninheritedMethods.size()="
					+ listLocIninheritedMethods.size());
			if (listLocIninheritedMethods.size() != 0) {

				ArrayList<Tuple2Method> lstTupMethSuper = new ArrayList<Tuple2Method>();
				// trouver les methodes get et set hors getter et setter interne
				ArrayList<String> lstMethodNameAdded = new ArrayList<String>();
				for (Method met : listLocIninheritedMethods) {
					String name = met.getName();
					if (!met.isSynthetic()
							&& (met.getParameterTypes().length == 0)
							&& (name.startsWith("get") || name.startsWith("is"))) {
						// && ((null == arrNotWeave) || !arrNotWeave
						// .contains(clazz + "." + name))) {
						boolean take0 = false;

						if (locToTrigger.size() == 0)
							take0 = true;
						for (String weave : locToTrigger) {
							Pattern pat = Pattern.compile(weave);
							if (pat.matcher(nameClazz + "." + name).find()) {
								take0 = true;
							}
						}
						for (String notWeave : locNotToTrigger) {
							Pattern pat = Pattern.compile(notWeave);
							if (pat.matcher(nameClazz + "." + name).find()) {
								take0 = false;
							}
						}
						if (take0) {
							String str2 = null;
							if (name.startsWith("get")) {
								str2 = name.substring(3);
							} else if (name.startsWith("is")) {
								str2 = name.substring(2);
							}
							boolean take = true;
							for (Tuple2 tup : lstTup) {
								if (tup.name.toLowerCase().equals(
										str2.toLowerCase())) {
									take = false;
								}
							}
							if (take) {
								String firstLetter = str2.substring(0, 1)
										.toLowerCase();
								String nameParam = firstLetter
										+ str2.substring(1);
								// traiter les doublons
								if (!lstMethodNameAdded.contains(met.getName())) {
									lstMethodNameAdded.add(met.getName());
									listLocIninheritedMethodsFiltered.add(met);
									lstTupMethAliasBis.add(new Tuple2(
											nameParam, met.getReturnType()
													.getName()));
									lstTupMethSuper.add(new Tuple2Method(met,
											met.getReturnType().getName()));
								}

							}
						}

					}

				}
				hmIninheritedMethods.put((Class<?>) myClazz,
						listLocIninheritedMethodsFiltered);
			}

			System.out.println("lstTupMethAliasBis.size()="
					+ lstTupMethAliasBis.size());

			// Fabrication du CompositeType
			ArrayList<Tuple2> allAttr = new ArrayList<Tuple2>();
			allAttr.addAll(lstTup);
			allAttr.addAll(lstTupMethAlias);

			allAttr.addAll(lstTupMethAliasBis);

			int sz = allAttr.size();
			System.out.println("nb ItemsNames=" + sz);
			String[] itemsName = new String[sz];
			String[] itemDescriptions = new String[sz];
			SimpleType<?>[] openTypes = new SimpleType[sz];

			int i = 0;
			for (Tuple2 tup : allAttr) {
				itemsName[i] = tup.name;
				itemDescriptions[i] = tup.name;
				if (tup.type.equals("java.lang.String")) {
					openTypes[i] = SimpleType.STRING;

				} else if (tup.type.equals("java.lang.Integer")
						|| tup.type.equals("int")) {
					openTypes[i] = SimpleType.INTEGER;

				} else if (tup.type.equals("java.lang.Double")
						|| tup.type.equals("double")) {
					openTypes[i] = SimpleType.DOUBLE;

				} else if (tup.type.equals("java.lang.Float")
						|| tup.type.equals("float")) {
					openTypes[i] = SimpleType.FLOAT;

				} else if (tup.type.equals("java.lang.Boolean")
						|| tup.type.equals("boolean")) {
					openTypes[i] = SimpleType.BOOLEAN;

				} else if (tup.type.equals("java.lang.Long")
						|| tup.type.equals("long")) {
					openTypes[i] = SimpleType.LONG;

				} else {
					openTypes[i] = SimpleType.STRING;

				}
				i++;
			}
			try {
				CompositeType cp = new CompositeType("mapValues",
						"Table of Values", itemsName, itemDescriptions,
						openTypes);
				hmClassCT.put(nameClazz, cp);
				// Creation du titre

				String shortNameClass = nameClazz.substring(nameClazz
						.lastIndexOf(".") + 1);
				String title = "####time" + sep + "identObject" + sep;

				// Classer les item
				for (String it : itemsName) {

					title += it + sep;
				}
				title += "countMeasures" + sep;
				hmOutFiles.put(nameClazz, title);
				getTrace().openTrace(title, shortNameClass + ".log");

				hmCurrentAge.put(nameClazz, 0L);
				;
			} catch (OpenDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public final synchronized List<Method> getAllGetIneheritedMethods(
			String nameClazz, List<Method> lstMeth, ClassLoader cl, int depth,
			String alias) {
		// depth ==0 => Only for this super Class; depht ==1 => for this super
		// Class and super.super class and so on
		Class<?> clazz = null;
		ArrayList<Method> returnLst = new ArrayList<Method>();
		ArrayList<String> locToTrigger = toTrigger.get(alias);
		ArrayList<String> locNotToTrigger = notToTrigger.get(alias);
		try {
			clazz = Class.forName(nameClazz, false, cl);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (depth == 0) {
			if (!nameClazz.equals("java.lang.Object")) {
				Method[] tabMethods = null;
				try {
					tabMethods = clazz.getDeclaredMethods();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (Method met1 : tabMethods) {
					if (!lstMeth.contains(met1))
						lstMeth.add(met1);
				}

				for (Method met : lstMeth) {
					// System.out.println("getAllGetIneheritedMethods Method="
					// + met.getName());
					String name = met.getName();
					if (!met.isSynthetic()
							&& (met.getParameterTypes().length == 0)
							&& (name.startsWith("get") || name.startsWith("is"))) {
						// && ((null == arrNotWeave) || !arrNotWeave
						// .contains(clazz + "." + name))) {
						boolean take0 = false;

						if (toTrigger.size() == 0)
							take0 = true;
						for (String weave : locToTrigger) {
							Pattern pat = Pattern.compile(weave);
							if (pat.matcher(nameClazz + "." + name).find()) {
								take0 = true;
							}
						}
						for (String notWeave : locNotToTrigger) {
							Pattern pat = Pattern.compile(notWeave);
							if (pat.matcher(nameClazz + "." + name).find()) {
								take0 = false;
							}
						}
						if (take0) {
							System.out
									.println("getAllGetIneheritedMethods candidates Methods  ="
											+ met.getName());
							returnLst.add((Method) met);

						}

					}
				}

			}
		} else {
			Class<?> superCl = clazz.getSuperclass();
			Method[] tabMethods = null;
			try {
				tabMethods = clazz.getDeclaredMethods();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Method met1 : tabMethods) {
				if (!returnLst.contains(met1))
					returnLst.add(met1);
			}

			returnLst.addAll(getAllGetIneheritedMethods(superCl.getName(),
					returnLst, cl, depth - 1, alias));
		}
		System.out
				.println("getAllGetIneheritedMethods nb of candidate Methods :"
						+ returnLst.size());
		return returnLst;
	}

	
	// Les methodes de l objet utilisees dans le corps de l advice doivent
	// absolument etre exclues du weaving
	// pour eviter des boucles infinies.

	
	
	public void attributesToTrace(Object obj, Properties _props, String alias,
			int countMeasures, int frequenceMeasure)// TO Completee)
	{
		// System.out.println("Rentree dans le weaving");
		if (null == hmPropsFilled) {
			hmPropsFilled = new HashMap<String, Boolean>();
		}
		if (!hmPropsFilled.containsKey(alias)
				|| hmPropsFilled.get(alias) == false) {
			fillProps(_props, alias);

		}
		Object currentObject = obj;
		Class<?> clazz = currentObject.getClass();

		String nameClass = currentObject.getClass().getName();
		String shortName = nameClass.substring(nameClass.lastIndexOf(".") + 1);

		if (!hmObject.containsKey(currentObject)) {
			idObject++;
			hmObject.put(currentObject, idObject);
		}

		synchronized (GetBasicAttributesValues.class) {
			if (!hmInternalAttribute.containsKey(nameClass)
					&& !hmExternalAttribute.containsKey(nameClass)) {
				System.out.println("ajout class:" + nameClass);
				addClass(nameClass, clazz.getClassLoader(), alias);

			}

		}

		// recuperer le bean en synchronized

		// Verifie si on doit mettre a jour en fonction de la frequence

		// eviter une fuite dans l identityHashMap
		// String outFile=hmOutFiles.get(nameClass);

		// ouvrir au bon moment ( avec titre complet) plus bas

		// On va mettre a jour le Mbean avec les donnees de l'Object
		// Internal attributes
		// on cree un HashMap Local
		HashMap<String, Object> localMapCD = new HashMap<String, Object>(
				hmClassCT.get(nameClass).keySet().size());
		ArrayList<Tuple2> lstTupAttr = hmInternalAttribute.get(nameClass);
		int cptInterne = 0;
		for (Tuple2 tup : lstTupAttr) {
			cptInterne++;
			// System.out.println("traitement param interne :"+cptInterne+" name="+tup.name);
			Field field = null;
			try {
				field = clazz.getDeclaredField(tup.name);

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			field.setAccessible(true);
			try {

				if (tup.type.equals("int")
						|| tup.type.equals("java.lang.Integer")) {
					// On doit tester la valeur nulle
					try {
						localMapCD.put(tup.name,
								new Integer(field.getInt(currentObject)));
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new Integer(0));
					}
				} else if (tup.type.equals("long")
						|| tup.type.equals("java.lang.Long")) {
					try {
						localMapCD.put(tup.name,
								new Long(field.getLong(currentObject)));
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new Long(0l));
					}

				} else if (tup.type.equals("double")
						|| tup.type.equals("java.lang.Double")) {
					try {
						localMapCD.put(tup.name,
								new Double(field.getDouble(currentObject)));
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new Double(0d));
					}
				} else if (tup.type.equals("float")
						|| tup.type.equals("java.lang.Float")) {
					try {
						localMapCD.put(tup.name,
								new Float(field.getFloat(currentObject)));
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new Float(0f));
					}
				} else if (tup.type.equals("boolean")
						|| tup.type.equals("java.lang.Boolean")) {
					try {
						localMapCD.put(tup.name,
								new Boolean(field.getBoolean(currentObject)));
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new Boolean(false));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						localMapCD.put(tup.name, new Boolean(false));
					}
				} else {
					// use toString for other type
					try {
						localMapCD.put(tup.name, field.get(currentObject)
								.toString());
					} catch (NullPointerException e) {
						localMapCD.put(tup.name, new String(""));
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// External attributes

		ArrayList<Tuple2> lstTupMeth = hmExternalAttribute.get(nameClass);

		int cptExterne = 0;
		for (Tuple2 tup : lstTupMeth) {
			cptExterne++;
			// System.out.println("traitement param externe :"+cptExterne+" name="+tup.name);
			Method meth = null;
			// Suppression du debut "get" ou "is", remplacement de la
			// premiere lettre par sa minuscule

			String nameParam = "";
			if (tup.name.startsWith("get")) {
				nameParam = tup.name.substring(3);
				String firstLetter = nameParam.substring(0, 1).toLowerCase();
				nameParam = firstLetter + nameParam.substring(1);

			} else {
				nameParam = tup.name.substring(2);
				String firstLetter = nameParam.substring(0, 1).toLowerCase();
				nameParam = firstLetter + nameParam.substring(1);
			}
			try {

				meth = clazz.getDeclaredMethod(tup.name);

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			meth.setAccessible(true);

			try {

				Object ret = meth.invoke(currentObject, (Object[]) null);
				if (null == ret) {
					localMapCD.put(nameParam, "");
				} else {
					String typeExt = ret.getClass().getName();
					if (typeExt.equals("java.lang.Integer")) {
						localMapCD.put(nameParam, (Integer) ret);
					} else if (typeExt.equals("java.lang.Long")) {
						localMapCD.put(nameParam, (Long) ret);
					} else if (typeExt.equals("java.lang.Double")) {
						localMapCD.put(nameParam, (Double) ret);
					} else if (typeExt.equals("java.lang.Float")) {
						localMapCD.put(nameParam, (Float) ret);

					} else if (typeExt.equals("java.lang.Boolean")) {
						localMapCD.put(nameParam, (Boolean) ret);

					} else {
						// use toString for other type
						localMapCD.put(nameParam, ret.toString());
					}

				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		// Inherited Attribute

		ArrayList<Method> listMeths = hmIninheritedMethods.get(currentObject
				.getClass());
		// TODO

		if (null != listMeths) {
			for (Method meth : listMeths) {

				try {

					String nameMethod = meth.getName();
					String nameParam = "";
					if (nameMethod.startsWith("get")) {
						nameParam = nameMethod.substring(3);
						String firstLetter = nameParam.substring(0, 1)
								.toLowerCase();
						nameParam = firstLetter + nameParam.substring(1);

					} else {
						nameParam = nameMethod.substring(2);
						String firstLetter = nameParam.substring(0, 1)
								.toLowerCase();
						nameParam = firstLetter + nameParam.substring(1);
					}
					Object ret = meth.invoke(currentObject, (Object[]) null);
					if (null == ret) {
						localMapCD.put(nameParam, "");
					} else {
						String typeExt = ret.getClass().getName();
						if (typeExt.equals("java.lang.Integer")) {
							localMapCD.put(nameParam, (Integer) ret);
						} else if (typeExt.equals("java.lang.Long")) {
							localMapCD.put(nameParam, (Long) ret);
						} else if (typeExt.equals("java.lang.Double")) {
							localMapCD.put(nameParam, (Double) ret);
						} else if (typeExt.equals("java.lang.Float")) {
							localMapCD.put(nameParam, (Float) ret);

						} else if (typeExt.equals("java.lang.Boolean")) {
							localMapCD.put(nameParam, (Boolean) ret);

						} else {
							// use toString for other type
							localMapCD.put(nameParam, ret.toString());
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		try {
			CompositeDataSupport cp = new CompositeDataSupport(
					hmClassCT.get(nameClass), localMapCD);
			String valueCp = currentDate() + sep + shortName + "_"
					+ hmObject.get(currentObject) + sep;

			String currenTitle = hmOutFiles.get(nameClass);
			// take values as the same order of the title.
			String[] tabStrTitle = currenTitle.split(";");
			for (int ii = 2; ii < tabStrTitle.length; ii++) {
				
				if (!"countMeasures".equals(tabStrTitle[ii])) {
					Object obj1 = cp.get(tabStrTitle[ii]);
					valueCp += obj1.toString() + sep;
				}
			}
			valueCp += countMeasures + sep;
			// for (Object obj1 : cp.values()) {
			// valueCp += obj1.toString() + sep;
			// }
			// System.out.println("valueCp="+valueCp);

			getTrace().append(currenTitle, valueCp);

			if (countMeasures >= Integer.MAX_VALUE - frequenceMeasure - 1)
				readCounter(obj, true);
			// TODO avec getInstance.open / append
			// outFile.append(valueCp+"\n");
		} catch (OpenDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
