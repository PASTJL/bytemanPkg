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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jlp.byteman.commons.*;

public class DownloadService implements Runnable {
	private DwldSSH dwld;

	private HandlerSSH handler = null;
	private MySSHConnection myCnx = null;

	public DownloadService(DwldSSH dwld) {
		this.dwld = dwld;
		// Look for login/password/port for IDserver
		boolean cont = true;
		int i = 1;
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		while (cont && i <= Main.allServers.size()) {
			if (Main.propsCnx.getProperty("server." + i + ".IdServer")
					.equals(dwld.getIdServer())) {
				// Creer une connexion de type MySSHConnectio
				myCnx = new MySSHConnection(
						Main.propsCnx.getProperty("server." + i
								+ ".AddrServer"),
						Main.propsCnx.getProperty("server." + i + ".Port"),
						Main.propsCnx.getProperty("server." + i + ".Login"),
						Main.propsCnx.getProperty("server." + i
								+ ".Password"),
								Main.propsCnx.getProperty("server." + i
										+ ".RootPassword")		);

				handler = new HandlerSSH(myCnx, defaultTimeOut);
				cont = false;
			} else {
				i++;
			}
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (null == myCnx || null == handler)
			return;

		String remoteFiles = dwld.getRemoteFiles();
		String howmany = dwld.getHowmany();
		String localDirectory = dwld.getLocalDirectory();
		String action = dwld.getAction();
		String[] realRemDirs = null;
		// test of repository
		boolean jarSuccess = false;
		if (remoteFiles.endsWith("/") || remoteFiles.endsWith("\\")) {
			// on compresse le repertoire avec jar
			realRemDirs = listDirs(
					remoteFiles.replaceAll("<server>", dwld.getIdServer()),
					howmany);
			for (String remDir : realRemDirs) {
				String tmpRemoteFile = jar(remDir);
				if (null != tmpRemoteFile) {

					jarSuccess = true;
					Pattern reg = Pattern.compile("/[^/]+/$");
					Pattern reg2 = Pattern.compile("[^/]+");
					Matcher match1 = reg.matcher(remDir);
					String ext1 = null;
					String ext2 = null;
					if (match1.find()) {
						ext1 = match1.group();
						Matcher match2 = reg2.matcher(ext1);
						if (match2.find()) {
							ext2 = match2.group();
						}
					}

					String localFile = localDirectory + ext2 + ".jar";
					handler.download(localFile, tmpRemoteFile);
					// dejarer zt effacer le jar file
					extract(localFile);
					new File(localFile).delete();
					SearchDirFile.deleteDir(new File(new File(localFile)
							.getParent() + File.separator + "META-INF"));
					// detruire le jar distant
					String command="rm -f "+tmpRemoteFile;
					handler.executeCommand(command);

				}
			}
		}
		if (!jarSuccess) {
			// Download classique de fichiers
			String[] realRemFiles = listFiles(
					remoteFiles.replaceAll("<server>", dwld.getIdServer()),
					howmany);
			System.out.println("Files to download");
			for(int i=0;i< realRemFiles.length;i++){
				System.out.println(realRemFiles[i]);
			}
			switch (dwld.getAction()) {
			case "Compress_NoPrefix":
				for(int i=0;i< realRemFiles.length;i++){
					
					int idx=realRemFiles[i].lastIndexOf("/");
					
					String fileOnly=realRemFiles[i].substring(idx+1);
					handler.download(dwld.getLocalDirectory()+fileOnly, realRemFiles[i],true,Main.packager.getProperty("ssh.download.command", "scp -f")+" ");
				}
				break;
			case "Compress_Prefix":
				for(int i=0;i< realRemFiles.length;i++){
					int idx=realRemFiles[i].lastIndexOf("/");
					String fileOnly=realRemFiles[i].substring(idx+1);
					String newName=dwld.getIdServer()+"_"+fileOnly;
					handler.download(dwld.getLocalDirectory()+newName, realRemFiles[i],true,Main.packager.getProperty("ssh.download.command", "scp -f")+" ");
				}
				break;
			case "NoCompress_NoPrefix":
				for(int i=0;i< realRemFiles.length;i++){
					int idx=realRemFiles[i].lastIndexOf("/");
					String fileOnly=realRemFiles[i].substring(idx+1);
					handler.download(dwld.getLocalDirectory()+fileOnly, realRemFiles[i]);
				}
				break;
			default : // NoCompress_prefix
				for(int i=0;i< realRemFiles.length;i++){
					int idx=realRemFiles[i].lastIndexOf("/");
					String fileOnly=realRemFiles[i].substring(idx+1);
					String newName=dwld.getIdServer()+"_"+fileOnly;
					handler.download(dwld.getLocalDirectory()+newName, realRemFiles[i]);
				}
				break;
			}
		}

	}

	
	private void extract(String jf) {

		File file = new File(jf);
		File rootPar = file.getParentFile();
		JarFile jarF = null;
		try {
			jarF = new JarFile(new File(jf));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Enumeration<JarEntry> jarEntries = (Enumeration<JarEntry>) jarF
				.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			File f = new java.io.File(rootPar + java.io.File.separator
					+ jarEntry.getName());
			if (jarEntry.isDirectory()) {
				if (!f.exists())
					f.mkdirs();
			} else {
				InputStream is = null;
				try {
					is = jarF.getInputStream(jarEntry);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // get the input stream
				if (f.exists())
					f.delete();
				if (!f.getParentFile().exists())
					f.getParentFile().mkdirs();
				FileOutputStream fos = null;
				try {
					fos = new java.io.FileOutputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] tabByte = new byte[10240];
				try {
					while (is.available() > 0) {
						// write contents of 'is' to 'fos'
						int num = is.read(tabByte);
						fos.write(tabByte, 0, num);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.close();
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		try {
			jarF.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String[] listFiles(String remFiles, String howmany) {
//		int idx=remFiles.lastIndexOf("/");
//		String rootDir=remFiles.substring(0,idx+1);
//		
		String cmd ="ls -art "+remFiles +" | tail -"+howmany ;
		String[] retour = handler.executeCommand(cmd).replaceAll("\n", " ")
				.split(" ");
		for(int i=0; i< retour.length;i++){
			retour[i]=retour[i].trim();
		}
		return retour;
	}
	private String[] listDirs(String remDir, String howmany) {
		// trouver
		System.out.println("remDir=" + remDir);
		Pattern reg = Pattern.compile("/[^/]+/$");
		Pattern reg2 = Pattern.compile("[^/]+");
		Matcher match1 = reg.matcher(remDir);
		String ext1 = null;
		String ext2 = null;
		if (match1.find()) {
			ext1 = match1.group();
			Matcher match2 = reg2.matcher(ext1);
			if (match2.find()) {
				ext2 = match2.group();
			}
		}

		String rootDir = remDir.substring(0, remDir.indexOf(ext1)) + "/";
		String cmd = "find " + rootDir + " -maxdepth 1  -type d -name \'"
				+ ext2 + "\' -printf  \"%T@ %Tc %p\\n\"  | sort -n | tail -n"
				+ howmany + " | grep -o \'" + rootDir + ".*\'";

		String[] retour = handler.executeCommand(cmd).replaceAll("\n", " ")
				.split(" ");
		String sep = retour[0].substring(0, 1);
		int len = retour.length;
		for (int i = 0; i < len; i++) {
			if (!retour[i].endsWith(sep)) {
				retour[i] = retour[i].trim() + sep;

			}
		}
		System.out.println("retour=" + retour[0]);
		return retour;

	}

	private String jar(String remDir) {

		// search with which command
		String cmd = "which jar 2>/dev/null | grep /bin/jar | head -1";
		String remJar = handler.executeCommand(cmd).trim();
		if (null == remJar ||  !remJar.endsWith("/bin/jar")) {
			// search by find
			cmd = "find /opt /usr /bin  -maxdepth 4 -type f -a -name jar 2>/dev/null | grep /bin/jar | head -1";
			remJar = handler.executeCommand(cmd).trim();
		}
		if (null != remJar && remJar.length() > 1) {
			// On compresse avec jar
			// trouver le nom du dernier repertoire
			Pattern reg = Pattern.compile("/[^/]+/$");
			Pattern reg2 = Pattern.compile("[^/]+");
			Matcher match1 = reg.matcher(remDir);
			String ext1 = null;
			String ext2 = null;
			if (match1.find()) {
				ext1 = match1.group();
				Matcher match2 = reg2.matcher(ext1);
				if (match2.find()) {
					ext2 = match2.group();
				}
			}

			String rootDir = remDir.substring(0, remDir.indexOf(ext1));
			String cmd2 = "cd " + rootDir + " ; " + remJar + " -cf  temp"
					+ ext2 + ".jar ./" + ext2 + "/";
			String tmpRet20 = handler.executeCommand(cmd2);

			System.out.println("test apres compression distante tmpret20="
					+ tmpRet20);
			return rootDir + "/" + "temp" + ext2 + ".jar";

		} else {
			// bin/jar not found forcing classic download of directory
			return null;
		}
	}

	private boolean myFilter(File file) {
		if (file.isFile()
				&& (file.getAbsolutePath().contains(
						File.separator + "csv" + File.separator)
						|| file.getAbsolutePath().contains(
								File.separator + "reports" + File.separator) || file
						.getAbsolutePath().contains(
								File.separator + "logs" + File.separator))
				&& file.getAbsolutePath().contains("temp")
				&& file.getAbsolutePath().endsWith(".jar"))
			return true;
		else
			return false;
	}
}
