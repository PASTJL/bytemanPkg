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
package jlp.byteman.packager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.jlp.scaviewer.ui.ScaChart;
import com.jlp.scaviewer.ui.ScaChartJDialog;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import jfx.messagebox.MessageBox;

public class Utils {

	public static void saveConfiguration(boolean open) {

		File f = new File(System.getProperty("workspace") + File.separator
				+ Main.currentProject + File.separator
				+ "bytemanpkg.properties");
		if (!open) {

			if (f.exists())
				f.delete();
		}
		File fBtm = new File(System.getProperty("workspace") + File.separator
				+ Main.currentProject + File.separator + "bytemanpkg.btm");
		if (fBtm.exists())
			fBtm.delete();
		// nettoyer les tab properties et btm
		Main.packRoot.getTaGenProps().setText("");
		Main.packRoot.getTaGenRules().setText("");
		String comments = "Modified on " + new Date().toString();
		// recuperation des valeurs du tab Configuration
		if (Main.packRoot.getGzip().isSelected()) {
			Main.propsConfRules.setProperty("bytemanpkg.gzip", "true");
		} else {
			Main.propsConfRules.setProperty("bytemanpkg.gzip", "false");
		}
		if (Main.packRoot.getFullPackage().isSelected()) {
			Main.propsConfRules.setProperty("bytemanpkg.fullPackage", "true");
		} else {
			Main.propsConfRules.setProperty("bytemanpkg.fullPackage", "false");
		}
		Main.propsConfRules.setProperty("bytemanpkg.cmdPID", Main.packRoot
				.getCmdPID().getText());
		Main.propsConfRules.setProperty("bytemanpkg.bytemanOpts", Main.packRoot
				.getBytemanOptsTf().getText());
		// Verification que cela soit des repertoires commencant et finisant par
		// le separateur
		String dirWork = Main.packRoot.getTfDirWork().getText();
		if (!dirWork.endsWith("/") && dirWork.contains("/")) {
			dirWork += "/";
		} else if (!dirWork.endsWith("\\") && dirWork.contains("\\")) {
			dirWork += "\\";
		}
		Main.packRoot.getTfDirWork().setText(dirWork);

		String dirLogs = Main.packRoot.getTfDirLogs().getText();
		if (!dirLogs.endsWith("/") && dirLogs.contains("/")) {
			dirLogs += "/";
		} else if (!dirLogs.endsWith("\\") && dirLogs.contains("\\")) {
			dirLogs += "\\";
		}
		Main.packRoot.getTfDirLogs().setText(dirLogs);

		Main.propsConfRules.setProperty("bytemanpkg.dirWork", Main.packRoot
				.getTfDirWork().getText());
		Main.propsConfRules.setProperty("bytemanpkg.dirLogs", Main.packRoot
				.getTfDirLogs().getText());

		Main.propsConfRules.setProperty("bytemanpkg.csvSep", Main.packRoot
				.getTfCsv().getText());
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(f);
			Main.propsConfRules.store(fout, comments);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != fout)
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "r");
			int len = (int) raf.length();
			byte[] tab = new byte[len];
			raf.readFully(tab);
			Main.packRoot.getTaGenProps().setEditable(true);
			Main.packRoot.getTaGenProps().setText(new String(tab));
			Main.packRoot.getTaGenProps().setEditable(false);
			raf.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != raf) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		generateBtmFile();

		if (Main.propsConfRules.getProperty("bytemanpkg.fullPackage", "false")
				.equals("false")) {

			zipMinimalInjar();
		} else {

			zipInjar();
		}
		fillLocalFiles();
	}

	private static void zipInjar() {

		// On met bytemanpkg.properties dans mybyteman.jar
		copy(new File(Main.root + File.separator + "lib" + File.separator
				+ "mypreparebyteman.jar"), new File(Main.workspace
				+ File.separator + Main.currentProject + File.separator
				+ "mybyteman.jar"));
		File btmToAdd = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "bytemanpkg.btm");
		File propsPkgToAdd = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator
				+ "bytemanpkg.properties");
		System.out.println("File : " + btmToAdd + " exists ?");
		if (btmToAdd.exists()) {
			try {
				System.out.println("File : " + btmToAdd + " exists");

				addFilesToExistingZip(new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "mybyteman.jar"), new File[] { propsPkgToAdd,
						btmToAdd });
				// fill the Tab generated Rules
				RandomAccessFile raf2 = new RandomAccessFile(btmToAdd, "rw");
				int len = (int) raf2.length();
				byte[] tab = new byte[len];
				raf2.readFully(tab);
				raf2.close();
				String str = new String(tab);
				Main.packRoot.getTaGenRules().setText(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// On met bytemanpkg.properties dans helper

		File helperPkg = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "sys" + File.separator
				+ "helperPkg.jar");
		if (helperPkg.exists())
			helperPkg.delete();
//		copy(new File(Main.root + File.separator + "lib" + File.separator
//				+ "helperPkg.jar"), new File(Main.workspace + File.separator
//				+ Main.currentProject + File.separator + "sys" + File.separator
//				+ "helperPkg.jar"));
//		if (propsPkgToAdd.exists()) {
//
//			System.out.println("File : " + btmToAdd + " exists");
//
//			try {
//				addFilesToExistingZip(new File(Main.workspace + File.separator
//						+ Main.currentProject + File.separator + "sys"
//						+ File.separator + "helperPkg.jar"),
//						new File[] { propsPkgToAdd });
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

	}

	private static void zipMinimalInjar() {

		copy(new File(Main.root + File.separator + "lib" + File.separator
				+ "mybasebyteman.jar"), new File(Main.workspace
				+ File.separator + Main.currentProject + File.separator
				+ "mybyteman.jar"));
		String pathToHelperPkg = Main.root + File.separator + "lib"
				+ File.separator + "helperPkg.jar";
		File btmToAdd = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "bytemanpkg.btm");
		File propsPkgToAdd = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator
				+ "bytemanpkg.properties");
		System.out.println("File : " + btmToAdd + " exists ?");
		if (btmToAdd.exists()) {
			Set<String> helpers = new HashSet<String>();
			try {
				System.out.println("File : " + btmToAdd + " exists");

				addFilesToExistingZip(new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "mybyteman.jar"), new File[] { propsPkgToAdd,
						btmToAdd });
				// fill the Tab generated Rules

				RandomAccessFile raf2 = new RandomAccessFile(btmToAdd, "rw");
				int len = (int) raf2.length();
				byte[] tab = new byte[len];
				raf2.readFully(tab);
				raf2.seek(0);

				String line = "";
				while ((line = raf2.readLine()) != null) {
					if (line.trim().startsWith("HELPER")) {
						String helper = line.trim().split("HELPER")[1].trim();
						if (!helper.equals("jlp.byteman.helper.MyHelper")) {
							helpers.add(helper);
						}
					}
				}
				raf2.close();
				String str = new String(tab);
				Main.packRoot.getTaGenRules().setText(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Add complemantary helpers collected in the set
			ZipOutputStream out;
			try {
				File tempFile = new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "mybyteman.jar" + ".tmp");

				if (tempFile.exists())
					tempFile.delete();

				copy(new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "mybyteman.jar"), tempFile);
				out = new ZipOutputStream(new FileOutputStream(new File(
						Main.workspace + File.separator + Main.currentProject
								+ File.separator + "mybyteman.jar")));
				ZipInputStream zin = new ZipInputStream(new FileInputStream(
						tempFile));
				ZipEntry entry = zin.getNextEntry();
				byte[] buf = new byte[1024];
				while (entry != null) {
					String name = entry.getName();
					out.putNextEntry(new ZipEntry(name));
					// Transfer bytes from the ZIP file to the output file
					int len;
					while ((len = zin.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					entry = zin.getNextEntry();
				}
				zin.close();
				for (String helper : helpers) {
					URL url;
					try {

						String fullPath = helper.replaceAll("\\.", "/");
						int idx = fullPath.lastIndexOf("/");
						String rep = fullPath.substring(0, idx);
						String className = fullPath.substring(0, idx + 1);

						String strUrl = "jar:file:" + pathToHelperPkg + "!/"
								+ fullPath + ".class";
						url = new URL(strUrl);
						InputStream is = url.openStream();

						entry = new ZipEntry(fullPath + ".class");
						out.putNextEntry(entry);
						int len;
						while ((len = is.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						// Complete the entry
						is.close();
						// Search for optionnal INNER Classes name =>
						// className$....class
						Pattern pat = Pattern.compile(fullPath
								+ "\\$[^\\.]+\\.class");

						List<String> ret = searchFileInZip(pat, new ZipFile(
								pathToHelperPkg));
						for (String nameFile : ret) {

							strUrl = "jar:file:" + pathToHelperPkg + "!/"
									+ nameFile;
							url = new URL(strUrl);
							is = url.openStream();
							entry = new ZipEntry(nameFile);
							out.putNextEntry(entry);

							while ((len = is.read(buf)) > 0) {
								out.write(buf, 0, len);
							}
							is.close();
						}
						out.closeEntry();

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				out.close();
				if (tempFile.exists())
					tempFile.delete();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		// On met bytemanpkg.properties dans helper
//
//		File helperPkg = new File(Main.workspace + File.separator
//				+ Main.currentProject + File.separator + "sys" + File.separator
//				+ "helperPkg.jar");
//		if (helperPkg.exists())
//			helperPkg.delete();
//		copy(new File(Main.root + File.separator + "lib" + File.separator
//				+ "helperPkg.jar"), new File(Main.workspace + File.separator
//				+ Main.currentProject + File.separator + "sys" + File.separator
//				+ "helperPkg.jar"));
//		if (propsPkgToAdd.exists()) {
//
//			System.out.println("File : " + btmToAdd + " exists");
//
//			try {
//				addFilesToExistingZip(new File(Main.workspace + File.separator
//						+ Main.currentProject + File.separator + "sys"
//						+ File.separator + "helperPkg.jar"),
//						new File[] { propsPkgToAdd });
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	public static void copy(File inputFile, File outputFile) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(inputFile);
			out = new FileOutputStream(outputFile);
			int c = 0;
			byte[] buf = new byte[16384];
			boolean bool = true;
			while (bool) {
				c = in.read(buf);
				if (c != -1)

					out.write(buf, 0, c);

				else
					bool = false;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.flush();

				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void addFilesToExistingZip(File zipFile, File[] files)
			throws IOException {
		// get a temp file
//		File tempFile = new File(Main.workspace + File.separator
//				+ Main.currentProject + File.separator + zipFile.getName()
//				+ ".tmp");
		File tempFile = new File( zipFile.getAbsolutePath()+".tmp");
				
		if (tempFile.exists())
			tempFile.delete();
		// File tempFile = File.createTempFile(zipFile.getName(),".tmp",new
		// File(Main.workspace+File.separator+Main.currentProject+File.separator));//TODO
		// delete it, otherwise you cannot rename your existing zip to it.

		copy(new File(zipFile.getAbsolutePath()), tempFile);

		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			for (File f : files) {
				if (f.getName().equals(name)) {
					notInFiles = false;
					break;
				}
			}
			if (notInFiles) {
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		// Close the streams
		zin.close();
		// Compress the files
		for (int i = 0; i < files.length; i++) {
			InputStream in = new FileInputStream(files[i]);
			// Add ZIP entry to output stream.
			if (files[i].getName().equals("bytemanpkg.btm")) {
				out.putNextEntry(new ZipEntry("jlp/byteman/helper/"
						+ files[i].getName()));
			} else {
				out.putNextEntry(new ZipEntry("jlp/byteman/helper/"
						+ files[i].getName()));
			}
			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// Complete the entry
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}

	private static void generateBtmFile() {
		File fBtm = new File(System.getProperty("workspace") + File.separator
				+ Main.currentProject + File.separator + "bytemanpkg.btm");
		if (fBtm.exists())
			fBtm.delete();
		RandomAccessFile rafBtm = null;
		try {
			rafBtm = new RandomAccessFile(fBtm, "rw");
			rafBtm.getChannel().truncate(0);
			String[] listRules = Main.propsConfRules.getProperty(
					"listRulesChosen", "").split(";");
			System.out.println("rl :" + listRules[0]);
			for (String rl : listRules) {
				if (rl.trim().length() > 0) {

					// recuperer le template :
					String fileName = Main.root
							+ File.separator
							+ "templates"
							+ File.separator
							+ "byteman"
							+ File.separator
							+ Main.bytemanRules.getProperty(rl.split("_")[0]
									+ ".tpl");

					File f = new File(Main.root
							+ File.separator
							+ "templates"
							+ File.separator
							+ "byteman"
							+ File.separator
							+ Main.bytemanRules.getProperty(rl.split("_")[0]
									+ ".tpl"));
					RandomAccessFile raf = null;
					String tplStr = "";
					try {
						// System.out.println("f.getCannonicalNanme => "+f.getAbsolutePath());
						raf = new RandomAccessFile(f, "r");
						int len = (int) raf.length();
						byte[] tab = new byte[len];
						raf.readFully(tab);
						tplStr = new String(tab);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (null != raf) {
							try {
								raf.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					rafBtm.writeBytes(traiterRule(rl, tplStr));
					rafBtm.writeBytes("\n#####################################################################################################\n");
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {
			try {
				rafBtm.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/** return the String with regex completed */
	private static String traiterRule(String rule, String strTpl) {

		// System.out.println("Utils.traiterRule rule="+rule+" \n; strTpl="+strTpl);
		String alias = rule.split("_")[1];
		// recuperer la liste des methodes
		String[] methods = Main.propsConfRules.getProperty(
				rule + ".listMethods", "").split(";");
		String location = null;

		String doStatement = "";
		String bindStatement = "";
		boolean isTraitedBindStatement = false;
		boolean isTraitedDostatement = false;
		String ifStatement = "TRUE";
		String helperClass = "jlp.byteman.helper.MyHelper";
		Set<Entry<Object, Object>> entries = Main.propsConfRules.entrySet();
		if (Main.propsConfRules.containsKey(rule + ".location")) {
			location = Main.propsConfRules.getProperty(rule + ".location", "");
		}

		if (Main.propsConfRules.containsKey(rule + ".ifStatement")) {
			ifStatement = Main.propsConfRules.getProperty(
					rule + ".ifStatement", "");
		}
		for (Entry<Object, Object> entry : entries) {

			// traitement du bindStatement
			if (((String) entry.getKey()).startsWith(rule
					+ ".bindStatement.line")
					&& !isTraitedBindStatement) {
				isTraitedBindStatement = true;
				boolean cont = true;
				int i = 0;
				while (cont) {
					if (Main.propsConfRules.containsKey(rule
							+ ".bindStatement.line" + i)) {

						String tmp = Main.propsConfRules.getProperty(rule
								+ ".bindStatement.line" + i);

						bindStatement += "\t\t" + tmp;
						if (bindStatement.substring(bindStatement.length() - 1)
								.equals(";")) {
							bindStatement += "\n";
						} else {
							bindStatement += ";\n";
						}

					} else {
						cont = false;
					}
					i++;
				}

			}
			// traitement du doStatement
			else if (((String) entry.getKey()).startsWith(rule
					+ ".doStatement.line")
					&& !isTraitedDostatement) {
				isTraitedDostatement = true;
				boolean cont = true;
				int i = 0;
				while (cont) {
					if (Main.propsConfRules.containsKey(rule
							+ ".doStatement.line" + i)) {

						String tmp = Main.propsConfRules.getProperty(rule
								+ ".doStatement.line" + i);

						doStatement += "\t\t" + tmp;
						if (doStatement.substring(doStatement.length() - 1)
								.equals(";")) {
							doStatement += "\n";
						} else {
							doStatement += ";\n";
						}

					} else {
						cont = false;
					}
					i++;
				}

			}
		}

		if (Main.propsConfRules.containsKey(rule + ".helperClass")) {
			helperClass = Main.propsConfRules.getProperty(
					rule + ".helperClass", "");
		}
		String retour = "\n";
		// regexp =>
		// e("^(\\w+\\s+)?(^?\\w+\\.)+\\w+(\\s*\\((\\s*\\w+\\s*,?)*\\))?$");
		// cas possible :
		// String ^package1.package2.Myclass.method1(String,Integer)
		// ^package1.package2.Myclass.method1(String,Integer)
		// package1.package2.Myclass.method1(String,Integer)
		// package1.package2.Myclass.method1
		// Myclass.method1
		// String Myclass.method1
		// String Myclass.method1(String,Integer)

		// rechercher d'abord la chaine MyClass.method dans les cas ci dessus
		Pattern patClassdotMethod = Pattern.compile(Main.packager
				.getProperty("patClassdotMethod"));

		// Sortir le package + la classe =>
		Pattern patPackageDotClass = Pattern.compile(Main.packager
				.getProperty("patPackageDotClass"));

		// Sortir la methode et les parametres eventuels =>
		Pattern patMethodEtParam = Pattern.compile(Main.packager
				.getProperty("patMethodEtParam"));

		// Sortir le return type eventuel
		Pattern patReturn = Pattern.compile(Main.packager
				.getProperty("patReturn"));

		System.out.println("methods[0] =>" + methods[0]);
		int i = 0;

		for (String meth : methods) {

			String strClass = "";
			// remplacement de la <CLASS>
			Matcher match0 = patPackageDotClass.matcher(meth);
			String strTplLoc = strTpl;
			if (match0.find()) {
				strClass = match0.group();
				// Supprimer le . final
				int idx = strClass.lastIndexOf(".");
				strClass = strClass.substring(0, idx);
				strTplLoc = strTplLoc.replaceAll("<CLASS>", strClass);
			}

			// remplacement de : <METHOD>

			// recherche d un type retour eventuel
			String strRet = "";
			match0 = patReturn.matcher(meth);
			if (match0.find()) {
				strRet = match0.group();
			}

			// Method et params
			String strMethParams = "";
			match0 = patMethodEtParam.matcher(meth);
			if (match0.find()) {
				strMethParams = match0.group();

				strTplLoc = strTplLoc
						.replaceAll("<METHODSHORT>", strMethParams);
			}
			if (strRet.equals("")) {
				strTplLoc = strTplLoc.replaceAll("<METHOD>", strMethParams);

			} else {
				strTplLoc = strTplLoc.replaceAll("<METHOD>", strRet + " "
						+ strMethParams);
			}
			// Remplacement de <ALIAS>

			strTplLoc = strTplLoc.replaceAll("<ALIAS>", alias);
			// Remplacement de <COUNTER>
			strTplLoc = strTplLoc.replaceAll("<COUNTER>",
					Integer.toString(Main.counterRules));
			Main.counterRules++;

			// Remplacement eventuel de locations
			if (null != location) {
				strTplLoc = strTplLoc.replaceAll("<LOCATION>", location);
			}
			if (null != ifStatement) {
				strTplLoc = strTplLoc.replaceAll("<IFSTATEMENT>", ifStatement);
			}
			// Remplacement eventuel de binds la premiere ligne doit inclure
			// BIND
			if (null != bindStatement) {
				strTplLoc = strTplLoc.replaceAll("<BINDSTATEMENT>",
						bindStatement);
			}

			// Remplacement eventuel de returns
			if (null != doStatement) {
				strTplLoc = strTplLoc.replaceAll("<DOSTATEMENT>", doStatement);
			}
			strTplLoc = strTplLoc.replaceAll("<HELPER>", helperClass);
			retour += "\n#___________ "
					+ rule
					+ " => "
					+ strClass
					+ "."
					+ strMethParams
					+ " __________________________________________________________\n"
					+ strTplLoc;
		}
		// Remplacement eventuel de variable
		retour = fillExtraVariable(retour, rule);
		return retour;
	}

	private static String fillExtraVariable(String retour, String rule) {

		// chercher toutes les cles commencant par le nom de la regle.
		HashMap<String, String> hmReplace = new HashMap<String, String>();
		Set<Entry<Object, Object>> entries = Main.bytemanRules.entrySet();
		for (Entry<Object, Object> entry : entries) {
			String key = (String) entry.getKey();
			if (key.contains(rule.split("_")[0])) {
				// on regarde si la regle a 3 champ
				if (((String) entry.getValue()).split(";").length == 3) {
					// on traite la substition
					// recupere la valeur de la cle dans bytemanRules
					String cle = ((String) entry.getValue()).split(";")[0];

					String regexToSubstitute = ((String) entry.getValue())
							.split(";")[2];

					String valueToSubstitute = Main.propsConfRules.getProperty(
							rule + "." + cle, "");

					retour = retour.replaceAll(regexToSubstitute,
							valueToSubstitute);

				}
			}
		}

		return retour;
	}

	private static List<String> searchFileInZip(Pattern pat, ZipFile zfile) {
		ArrayList<String> ret = new ArrayList<String>();
		Enumeration<? extends ZipEntry> enumer;

		for (enumer = zfile.entries(); enumer.hasMoreElements();) {
			ZipEntry e = (ZipEntry) enumer.nextElement();
			String name = e.getName();
			Matcher match = pat.matcher(name);
			if (match.find()) {
				ret.add(name);
			}
		}

		return ret;
	}

	private static void fillLocalFiles() {
		File[] filesBoot = null;
		File[] filesSys = null;
		// Mettre a jour les parametres <PID> et <bytemanpkg.dirWork>
		File f = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "pkgbminstall.sh");
		if (f.exists())
			f.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "pkgbminstall.sh"), f);

		File f2 = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "attachMBean.sh");
		if (f2.exists())
			f2.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "attachMBean.sh"), f2);
		File dirBoot = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "boot");
		filesBoot = dirBoot.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".jar");
			}
		});
		String byteman_boot = "";
		{
			if (filesBoot.length > 0) {
				for (File file1 : filesBoot) {
					byteman_boot += "$BYTEMAN_HOME/lib/" + file1.getName()
							+ " ";
				}
			}
		}

		File dirSys = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "sys");
		filesSys = dirSys.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".jar");
			}
		});
		String byteman_sys = "";
		{
			if (filesSys.length > 0) {
				for (File file1 : filesSys) {
					byteman_sys += "$BYTEMAN_HOME/lib/" + file1.getName()
							+ " ";
				}
			}
		}
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			long ln = f.length();
			byte[] tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			String str = new String(tabBytes);
			str = str.replace("<bytemanpkg.dirWork>", Main.propsConfRules
					.getProperty("bytemanpkg.dirWork", "/tmp/"));
			str = str.replace("<bytemanOpts>", Main.propsConfRules.getProperty(
					"bytemanpkg.bytemanOpts", ""));
			// trouver le PID
			String pidStr = Main.packRoot.getCmdPID().getText();
			String PID = "";
			if (pidStr.matches("PID=")) {
				PID = pidStr.split("=")[1];
			} else {
				PID = "`" + pidStr + "`";
			}
			str = str.replace("<PID>", PID);
			str = str.replace("<PORT>", Main.propsConfRules.getProperty(
					"bytemanpkg.portListener", "9091"));
			str = str.replace("<PID>", PID);
			str = str.replace("<BYTEMAN_BOOT>", byteman_boot);
			str = str.replace("<BYTEMAN_SYS>", byteman_sys);
			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();

			raf = new RandomAccessFile(f2, "rw");
			ln = f2.length();
			tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			str = new String(tabBytes);

			// trouver le PID
			pidStr = Main.packRoot.getCmdPID().getText();
			PID = "";
			if (pidStr.matches("PID=")) {
				PID = pidStr.split("=")[1];
			} else {
				PID = "`" + pidStr + "`";
			}
			str = str.replace("<bytemanpkg.dirWork>", Main.propsConfRules
					.getProperty("bytemanpkg.dirWork", "/tmp/"));
			str = str.replace("<PID>", PID);

			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "pkgbmsubmit.sh");
		if (f.exists())
			f.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "pkgbmsubmit.sh"), f);
		
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			long ln = f.length();
			byte[] tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			String str = new String(tabBytes);
			str = str.replace("<bytemanpkg.dirWork>",
					Main.propsConfRules.getProperty("bytemanpkg.dirWork"));
			str = str.replace("<bytemanOpts>", Main.propsConfRules.getProperty(
					"bytemanpkg.bytemanOpts", ""));
			str = str.replace("<PORT>", Main.propsConfRules.getProperty(
					"bytemanpkg.portListener", "9091"));
			// trouver les fichiers de boot sous <currentProject/boot

			str = str.replace("<BYTEMAN_BOOT>", byteman_boot);
			str = str.replace("<BYTEMAN_SYS>", byteman_sys);
			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "pkgbmunsubmit.sh");
		if (f.exists())
			f.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "pkgbmunsubmit.sh"), f);

		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			long ln = f.length();
			byte[] tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			String str = new String(tabBytes);
			str = str.replace("<bytemanpkg.dirWork>",
					Main.propsConfRules.getProperty("bytemanpkg.dirWork"));
			str = str.replace("<bytemanOpts>", Main.propsConfRules.getProperty(
					"bytemanpkg.bytemanOpts", ""));
			str = str.replace("<PORT>", Main.propsConfRules.getProperty(
					"bytemanpkg.portListener", "9091"));
			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "recupMD5.sh");
		if (f.exists())
			f.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "recupMD5.sh"), f);

		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			long ln = f.length();
			byte[] tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			String str = new String(tabBytes);
			str = str.replace("<bytemanpkg.dirWork>",
					Main.propsConfRules.getProperty("bytemanpkg.dirWork"));

			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "findUserAndPidAndJVM.sh");
		if (f.exists())
			f.delete();
		copy(new File(Main.root + File.separator + "scripts" + File.separator
				+ "findUserAndPidAndJVM.sh"), f);

		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			long ln = f.length();
			byte[] tabBytes = new byte[(int) ln];
			raf.read(tabBytes);
			String str = new String(tabBytes);
			String pidStr = Main.packRoot.getCmdPID().getText();
			String PID = "";
			if (pidStr.matches("PID=")) {
				PID = pidStr.split("=")[1];
			} else {
				PID = "`" + pidStr + "`";
			}
			str = str.replace("<bytemanpkg.dirWork>", Main.propsConfRules
					.getProperty("bytemanpkg.dirWork", "/tmp/"));
			str = str.replace("<PID>", PID);

			raf.getChannel().truncate(0);
			raf.writeBytes(str);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("End install mybyteman.jar");

	}

	public static void installByteman() {
		Utils.saveConfiguration(false);
		Main.packRoot.getCmdConsole().setText("");
		System.out.println("Begin install mybyteman.jar");

		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert de recupMD5

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-install.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}
					String localFile = Main.workspace + File.separator
							+ Main.currentProject + File.separator
							+ "recupMD5.sh";
					String remoteFile = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork") + "recupMD5.sh";

					handler.upload(localFile, remoteFile);
					String command = "chmod 777 " + remoteFile;
					handler.executeCommand(command);

					controle(handler);

					// pkgbminstall.sh

					command = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "pkgbminstall.sh";

					// on execute la derniere commande avec le bon user
					// String ret = handler.executeCommand(command);

					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Treatement of server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;
					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();

				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End install mybyteman.jar");
	}

	public static void submitByteman() {
		System.out.println("Begin submit  bytemanpkg.btm");
		Utils.saveConfiguration(false);

		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert des fichiers vers le serveur

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-submit.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}
					controle(handler);

					// on execute la commande
					String command = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "pkgbmsubmit.sh";
					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Treatement of server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;
					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();
				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End  submit  bytemanpkg.btm");

	}

	public static void unsubmitByteman() {

		System.out.println("Begin submit  bytemanpkg.btm");

		// Mettre a jour les parametres <PID> et <bytemanpkg.dirWork>

		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert des fichiers vers le serveur

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-submit.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}
					// controle(handler);

					String command = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "pkgbmunsubmit.sh";
					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Treatement of server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;
					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();
				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End unsubmit  bytemanpkg.btm");

	}

	private static boolean controle(HandlerSSH handler) {
		String bytemanHome = Main.propsConfRules.getProperty(
				"bytemanpkg.dirWork", "/tmp/");
		String lsCommand = "ls -l " + bytemanHome + "mybyteman.jar" + " "
				+ bytemanHome + "lib/byteman-install.jar" + " " + bytemanHome
				+ "lib/byteman.jar" + " " + bytemanHome
				+ "lib/byteman-submit.jar" + " " + bytemanHome
				+ "/bytemanpkg.btm" + " " + bytemanHome + "/pkgbminstall.sh"
				+ " " + bytemanHome + "/pkgbmsubmit.sh" + " " + bytemanHome
				+ "/pkgbmunsubmit.sh" + " " + bytemanHome + "/attachMBean.sh";

		List<String> listRegexp = new ArrayList<String>();
		listRegexp.add("/recupMD5.sh");//
		listRegexp.add("/mybyteman.jar");//
		listRegexp.add("/byteman.jar"); //
		listRegexp.add("/byteman-install.jar");//
		listRegexp.add("/byteman-submit.jar");//
		listRegexp.add("/bytemanpkg.btm");//
		listRegexp.add("/pkgbminstall.sh");//
		listRegexp.add("/pkgbmsubmit.sh");//
		listRegexp.add("/pkgbmunsubmit.sh");//
		listRegexp.add("/attachMBean.sh");//
		listRegexp.add("/findUserAndPidAndJVM.sh");//
		// creer <dir_install>/lib
		String commandMkdir = "mkdir "
				+ Main.propsConfRules.getProperty("bytemanpkg.dirWork")
				+ "/lib/ >/dev/null 2>&1";
		handler.executeCommand(commandMkdir);
		File dirBoot = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "boot");
		File[] filesBoot = dirBoot.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".jar");
			}
		});

		String byteman_boot = "";

		if (filesBoot.length > 0) {
			for (File file1 : filesBoot) {
				byteman_boot += bytemanHome + "lib/" + file1.getName() + " ";
				listRegexp.add("/" + file1.getName());
			}
		}

		File dirSys = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator + "sys");
		File[] filesSys = dirSys.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".jar");
			}
		});
		String byteman_sys = "";

		if (filesSys.length > 0) {
			for (File file1 : filesSys) {
				byteman_sys += bytemanHome + "lib/" + file1.getName() + " ";
				listRegexp.add("/" + file1.getName());
			}
		}

		lsCommand = (lsCommand + " " + byteman_sys + " " + byteman_boot).trim()
				+ " 2>/dev/null";
		String ret = handler.executeCommand(lsCommand).trim();
		// Execution du sumMd5
		String commandMD5 = Main.propsConfRules
				.getProperty("bytemanpkg.dirWork") + "recupMD5.sh 2>/dev/null";
		String retMD5 = handler.executeCommand(commandMD5).trim();
		String version = Main.packager.getProperty("bytemanVersion");
		HashMap<String, String> hmMD5 = new HashMap<String, String>();
		if (null != retMD5 && retMD5.trim().length() > 0) {
			String[] tabMD5 = retMD5.split(";");
			for (String str : tabMD5) {

				String[] tmpTab = str.split(":");
				// System.out.println("tmpTab[0]="+tmpTab[0]);
				// System.out.println("tmpTab[1]="+tmpTab[1]);
				hmMD5.put(tmpTab[0], tmpTab[1]);
			}
		}

		String myPath = Main.workspace + File.separator + Main.currentProject;

		for (String strFile : listRegexp) {
			String fileOnly = strFile.substring(strFile.lastIndexOf("/") + 1);

			if (!ret.contains(strFile)) {
				// On upload

				switch (fileOnly) {
				case "byteman.jar":

					String localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-" + version + ".jar";
					String remoteFile = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "/lib/byteman.jar";
					handler.upload(localFile, remoteFile);
					String command = "chmod 777 " + remoteFile;
					handler.executeCommand(command);
					break;
				case "byteman-install.jar":
					localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-install-" + version
							+ ".jar";
					remoteFile = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "/lib/byteman-install.jar";
					handler.upload(localFile, remoteFile);
					command = "chmod 777 " + remoteFile;
					handler.executeCommand(command);
					break;
				case "byteman-submit.jar":
					localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-submit-" + version
							+ ".jar";
					remoteFile = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "/lib/byteman-submit.jar";
					handler.upload(localFile, remoteFile);
					command = "chmod 777 " + remoteFile;
					handler.executeCommand(command);
					break;
				default:
					// trouver sous myPath
					if (new File(myPath + File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + fileOnly;
						remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork") + fileOnly;
						handler.upload(localFile, remoteFile);
						command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					} else if (new File(myPath + File.separator + "sys"
							+ File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + "sys"
								+ File.separator + fileOnly;
						remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "lib/"
								+ fileOnly;
						handler.upload(localFile, remoteFile);
						command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					} else if (new File(myPath + File.separator + "boot"
							+ File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + "boot"
								+ File.separator + fileOnly;
						remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "lib/"
								+ fileOnly;
						handler.upload(localFile, remoteFile);
						command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					}

					break;

				}

			} else {
				// on controlle le checksum

				switch (fileOnly) {
				case "byteman.jar":

					String localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-" + version + ".jar";
					String localMD5 = new GetMD5ForFile().checksum(new File(
							localFile), false);
					String remoteMD5 = hmMD5.get(fileOnly);

					if (localMD5.equals(remoteMD5)) {
						System.out.println("MD5 of file : " + fileOnly
								+ " are the same in local and remote");
					} else {
						System.out
								.println("MD5 of file : "
										+ fileOnly
										+ " are different in local and remote, proceeding upload");
						String remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "/lib/byteman.jar";
						handler.upload(localFile, remoteFile);
						String command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					}

					break;
				case "byteman-install.jar":
					localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-install-" + version
							+ ".jar";
					localMD5 = new GetMD5ForFile().checksum(
							new File(localFile), false);
					remoteMD5 = hmMD5.get(fileOnly);
					if (localMD5.equals(remoteMD5)) {
						System.out.println("MD5 of file : " + fileOnly
								+ " are the same in local and remote");
					} else {
						System.out
								.println("MD5 of file : "
										+ fileOnly
										+ " are different in local and remote, proceeding upload");
						String remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "/lib/byteman-install.jar";
						handler.upload(localFile, remoteFile);
						String command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					}
					break;
				case "byteman-submit.jar":
					localFile = Main.root + File.separator + "lib"
							+ File.separator + "byteman-submit-" + version
							+ ".jar";
					localMD5 = new GetMD5ForFile().checksum(
							new File(localFile), false);
					remoteMD5 = hmMD5.get(fileOnly);
					if (localMD5.equals(remoteMD5)) {
						System.out.println("MD5 of file : " + fileOnly
								+ " are the same in local and remote");
					} else {
						System.out
								.println("MD5 of file : "
										+ fileOnly
										+ " are different in local and remote, proceeding upload");
						String remoteFile = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "/lib/byteman-submit.jar";
						handler.upload(localFile, remoteFile);
						String command = "chmod 777 " + remoteFile;
						handler.executeCommand(command);
					}
					break;
				default:
					// trouver sous myPath
					if (new File(myPath + File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + fileOnly;
						localMD5 = new GetMD5ForFile().checksum(new File(
								localFile), false);
						remoteMD5 = hmMD5.get(fileOnly);
						if (localMD5.equals(remoteMD5)) {
							System.out.println("MD5 of file : " + fileOnly
									+ " are the same in local and remote");
						} else {
							System.out
									.println("MD5 of file : "
											+ fileOnly
											+ " are different in local and remote, proceeding upload");
							String remoteFile = Main.propsConfRules
									.getProperty("bytemanpkg.dirWork")
									+ fileOnly;
							handler.upload(localFile, remoteFile);
							String command = "chmod 777 " + remoteFile;
							handler.executeCommand(command);
						}
					} else if (new File(myPath + File.separator + "sys"
							+ File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + "sys"
								+ File.separator + fileOnly;
						localMD5 = new GetMD5ForFile().checksum(new File(
								localFile), false);
						remoteMD5 = hmMD5.get(fileOnly);
						if (localMD5.equals(remoteMD5)) {
							System.out.println("MD5 of file : " + fileOnly
									+ " are the same in local and remote");
						} else {
							System.out
									.println("MD5 of file : "
											+ fileOnly
											+ " are different in local and remote, proceeding upload");
							String remoteFile = Main.propsConfRules
									.getProperty("bytemanpkg.dirWork")
									+ "lib/"
									+ fileOnly;
							handler.upload(localFile, remoteFile);
							String command = "chmod 777 " + remoteFile;
							handler.executeCommand(command);
						}
					} else if (new File(myPath + File.separator + "boot"
							+ File.separator + fileOnly).exists()) {
						localFile = myPath + File.separator + "boot"
								+ File.separator + fileOnly;
						localMD5 = new GetMD5ForFile().checksum(new File(
								localFile), false);
						remoteMD5 = hmMD5.get(fileOnly);
						if (localMD5.equals(remoteMD5)) {
							System.out.println("MD5 of file : " + fileOnly
									+ " are the same in local and remote");
						} else {
							System.out
									.println("MD5 of file : "
											+ fileOnly
											+ " are different in local and remote, proceeding upload");
							String remoteFile = Main.propsConfRules
									.getProperty("bytemanpkg.dirWork")
									+ "lib/"
									+ fileOnly;
							handler.upload(localFile, remoteFile);
							String command = "chmod 777 " + remoteFile;
							handler.executeCommand(command);
						}
					}

					break;

				}

			}
		}

		return true;
	}

	public static void flushStreams() {
		System.out.println(" Begin flushing  all OutputStreams");
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert des fichiers vers le serveur

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-install.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}
					String command = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "attachMBean.sh flushAllOs";

					// on execute la derniere commande
					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Flushing OS on server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;
					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();

				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End flushing  all OutputStreams");

	}

	public static void reopenStreams() {

		System.out.println(" Begin reopening  all OutputStreams");
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert des fichiers vers le serveur

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-install.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);

					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}

					String command = Main.propsConfRules
							.getProperty("bytemanpkg.dirWork")
							+ "attachMBean.sh reOpenOs";

					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Flushing OS on server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;
					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();

				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End reopening all OutputStreams");

	}

	private static HashMap<String, String> findUserAndPidAndJVM(
			HandlerSSH handler) {
		HashMap<String, String> hmRet = new HashMap<String, String>();
		String command = Main.propsConfRules.getProperty("bytemanpkg.dirWork")
				+ "findUserAndPidAndJVM.sh ";
		String ret = handler.executeCommand(command);
		if (null == ret || ret.trim().length() == 0) {
			ret = handler.executeCommand(command);
		}
		System.out.println("ret findUserAndPidAndJVM=" + ret);
		String[] tab = ret.split(";");
		for (String str : tab) {
			if (str.trim().contains("=")) {
				String[] tmpTab = str.split("=");
				hmRet.put(tmpTab[0], tmpTab[1]);
				System.out.println(tmpTab[0] + " --> " + tmpTab[1]);
			}

		}

		return hmRet;
	}

	public static void activateDeActivateRules(ActionEvent event) {

		Button but = (Button) event.getSource();
		String action = but.getText();

		System.out.println(" Begin action : " + action);
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		// Tranfert des fichiers vers le serveur

		boolean cont = true;
		int i = 1;
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				if (Main.packRoot.getCbxRemoteServers().getSelectionModel()
						.getSelectedItem().equals("ALL")
						|| Main.propsCnx.getProperty(
								"server." + i + ".IdServer").equals(
								Main.packRoot.getCbxRemoteServers()
										.getSelectionModel().getSelectedItem())) {

					// transfert des fichiers mybyteman.jar
					// byteman-install.jar
					// pkgbminstall.sh et excecution de ce dernier
					MySSHConnection myCnx = new MySSHConnection(
							Main.propsCnx.getProperty("server." + i
									+ ".AddrServer"),
							Main.propsCnx.getProperty("server." + i + ".Port"),
							Main.propsCnx.getProperty("server." + i + ".Login"),
							Main.propsCnx.getProperty("server." + i
									+ ".Password"), Main.propsCnx
									.getProperty("server." + i
											+ ".RootPassword"));

					HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
					if (null == handler.sess) {
						MessageBox
								.show(Main.currentStage,
										"Error Connecting to : "
												+ myCnx.getHost()
												+ " with user => "
												+ myCnx.getUser()
												+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
										"Error Connection",
										MessageBox.ICON_ERROR);
						return;

					}

					String command = "";
					if (action.equals("Activate Rules")) {

						command = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "attachMBean.sh enableRules";
						but.setText("De-Activate Rules");
					} else {
						command = Main.propsConfRules
								.getProperty("bytemanpkg.dirWork")
								+ "attachMBean.sh disableRules";
						but.setText("Activate Rules");
					}

					HashMap<String, String> hmProcess = findUserAndPidAndJVM(handler);

					command = "JAVA_HOME=" + hmProcess.get("JDK_HOME")
							+ ";export JAVA_HOME;" + command;
					String ret = handler.executeCommand(command, hmProcess);

					String toSee = "Action : "
							+ action
							+ " on server => "
							+ Main.propsCnx.getProperty("server." + i
									+ ".IdServer") + "\n";
					String strCurrent = Main.packRoot.getCmdConsole().getText();
					String newText = strCurrent
							+ "\n#############################################\n"
							+ toSee + ret;

					Main.packRoot.getCmdConsole().setText(newText);
					Main.packRoot.getCmdConsole().appendText("");
					handler.close();

				}
				i++;
			} else
				cont = false;
		}

		System.out.println("End action : " + action);

	}

	public static void viewStaticCsv() {
		ScaChartJDialog.apply(Main.currentProject, false);

	}

	public static void viewDynCsv() {
		ScaChartJDialog.apply(Main.currentProject, true);

	}
}
