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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;


/**
 * Trace as Singleton and MBean for JMX access => reopening OutputStreams
 **/
public class Trace implements TraceMBean {

	public static Trace instance;
	private SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss.SSS");
	public boolean isZipped = false;
	// public FileOutputStream fos = null;

	private static Runtime runtime = Runtime.getRuntime();
	public boolean activeRules = true;

	public boolean exiting = false;
	public String signalCourant = "NONE";
	public static ConcurrentHashMap<String, String> hmLogs ;
	public static ConcurrentHashMap<String, BufferedWriter> hmBuffs;
	public static ConcurrentHashMap<String, String> hmLogsFullName ;
	public static Properties props ;

	public static synchronized Trace getInstance() {
		if (null == instance) {
			instance = new Trace();
			hmLogs = new ConcurrentHashMap<String, String>();
			hmBuffs = new ConcurrentHashMap<String, BufferedWriter>();
			hmLogsFullName = new ConcurrentHashMap<String, String>();
			 props = new Properties();
			// Charger le fichier byteman.properties avec le Classloader system
			InputStream in = ClassLoader
					.getSystemResourceAsStream("jlp/byteman/helper/bytemanpkg.properties");
			if (null == in) {
				System.out.println("bytemanpkg.properties can't not be found");
				// rechercher sous directory
				
			} else {
				try {
					props.load(in);
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			runtime.addShutdownHook(new WatchDog());
		}
		return instance;
	}

	// public synchronized boolean openTrace(String title, String nomFileLogs) {

	public synchronized boolean openTrace(String title, String nomFileLogs) {

		// synchronized (Trace.hmLogs) {
		if (Trace.hmLogs.containsKey(title)) {
			return false;
		} else {

			BufferedWriter buffOut = null;
			Locale.setDefault(Locale.ENGLISH);

			System.out
					.println("[Instrumentation Rules by JBoss/Byteman ] Deb Creation Trace os.name = "
							+ System.getProperty("os.name").toUpperCase());

			int i = nomFileLogs.lastIndexOf(".");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String dateF = sdf2.format(Calendar.getInstance().getTime());
			String dirLogs = props.getProperty("bytemanpkg.dirLogs", "/tmp");
			if (!dirLogs.endsWith(File.separator)) {
				dirLogs += File.separator;
			}
			String newNomFileLogs = dirLogs + nomFileLogs.substring(0, i) + "_"
					+ dateF + "." + nomFileLogs.substring(i + 1);

			System.out.println(Version.version);
			System.out
					.println("[Instrumentation Rules by JBoss/Byteman ] byteman.trace.nomFileLogs =  "
							+ nomFileLogs);
			System.out
					.println("[Instrumentation Rules by JBoss/Byteman ] byteman.trace.title =  "
							+ title);
			try {

				if ("false".equals(props.getProperty("bytemanpkg.gzip"))) {
					// myPrintWriter=new PrintWriter(new BufferedWriter(new
					// FileWriter(nomFileLogs,true)));
					buffOut = new BufferedWriter(new FileWriter(newNomFileLogs,
							true));
					isZipped = false;
				} else {
					newNomFileLogs += ".gz";
					FileOutputStream fos = new FileOutputStream(newNomFileLogs,
							true);
					isZipped = true;
					// myPrintWriter=new PrintWriter(new BufferedWriter (new
					// OutputStreamWriter( new GZIPOutputStream(fos))));
					GZIPOutputStream gzOs = null;
					gzOs = new GZIPOutputStream(fos);
					buffOut = new BufferedWriter(new OutputStreamWriter(gzOs));

				}

				// append(title+"\n");
				if (null != buffOut) {
					try {

						buffOut.write(title + "\n");
						buffOut.flush();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Ecriture KO, buffOut is null");
				}
				Trace.hmLogsFullName.put(title, newNomFileLogs);
				Trace.hmLogs.put(title, nomFileLogs);
				Trace.hmBuffs.put(title, buffOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		// }
	}

	public final synchronized void reOpenOs() {
		// On copie hmLogs dans un HashMap temporaire
		// On ferme les buffers courants
		ConcurrentHashMap<String, String> tmpHmLogs = new ConcurrentHashMap<String, String>(
				hmLogs);
		try {

			for (Map.Entry<String, BufferedWriter> entry : Trace.hmBuffs
					.entrySet()) {
				entry.getValue().flush();

				entry.getValue().close();
				Thread.sleep(10);
				// renaming closed file.

				String fStr = hmLogsFullName.get(entry.getKey());

				File f = new File(fStr);
				int idx = fStr.lastIndexOf(File.separator);
				String fStrDest = fStr.substring(0, idx) + File.separator
						+ "closed_" + fStr.substring(idx + 1);

				if (!f.renameTo(new File(fStrDest))) {
					System.out
							.println("Fail to rename, switch to copy file. Longer Arggg!");
					copy(f, new File(fStrDest));
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// on nettoie hmLogs
		Trace.hmLogs.clear();
		Trace.hmBuffs.clear();
		Trace.hmLogsFullName.clear();

		for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) tmpHmLogs
				.entrySet()) {

			openTrace(entry.getKey(), entry.getValue());
		}

	}

	 public final synchronized void append(String title, String myString) {
		if (null != Trace.hmBuffs.get(title)) {
			try {

				Trace.hmBuffs.get(title).write(myString + "\n");

				// Trace.hmBuffs.get(title).flush();

			} catch (IOException e) {
				try {
					Thread.sleep(100);
					Trace.hmBuffs.get(title).write(myString + "\n");

				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out
							.println("An operation of shutdown or flushing or reopening streams is pending");
				}

				//e.printStackTrace();
			}

		} else {
			// reouverture
			/*
			 * reouverture(); try{ buffOut.write(myString); flush(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
		}
	}

	public final synchronized void flush(String title) {
		// myPrintWriter.flush();
		try {
			//
			for (Map.Entry<String, BufferedWriter> entry : Trace.hmBuffs
					.entrySet()) {
				entry.getValue().flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final SimpleDateFormat getSdf() {
		return sdf;
	}

	public final void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public boolean isActiveRules() {
		return activeRules;
	}

	public void setActiveRules(boolean bool) {
		activeRules = bool;

	}

	public void enableRules() {
		setActiveRules(true);

	}

	public void disableRules() {
		setActiveRules(false);
	}

	public void flushAllOs() {

		// On fhush les buffers courants

		try {

			for (Map.Entry<String, BufferedWriter> entry : Trace.hmBuffs
					.entrySet()) {
				entry.getValue().flush();

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

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

}
