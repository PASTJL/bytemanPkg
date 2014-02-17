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

public class UploadService implements Runnable {
	private UpldSSH upld;

	private HandlerSSH handler =null ;
	private MySSHConnection myCnx=null ;
	public UploadService(UpldSSH upld){
		this.upld=upld;
		// Look for login/password/port for IDserver
		boolean cont = true;
		int i = 1;
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		while (cont && i <= Main.allServers.size()) {
			if (Main.propsCnx.getProperty("server." + i + ".IdServer").equals(upld.getIdServer())) {
				// Creer une connexion de type MySSHConnectio
				 myCnx = new MySSHConnection(
						 Main.propsCnx.getProperty("server." + i + ".AddrServer"),
						 Main.propsCnx.getProperty("server." + i + ".Port"),
						 Main.propsCnx.getProperty("server." + i + ".Login"),
						 Main.propsCnx.getProperty("server." + i + ".Password"),
						 Main.propsCnx.getProperty("server." + i + ".RootPassword") );

				 handler = new HandlerSSH(myCnx, defaultTimeOut);
				cont=false;
			}
			else {
				i++;
			}
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(null == myCnx || null == handler) return; 
		
		String localFile = upld.getLocalFile();
		int idx = upld.getLocalFile().lastIndexOf(File.separator);
		String fileOnly=upld.getLocalFile().substring(idx+1);
		String remoteFile = upld.getRemoteDirectory()+fileOnly;
		remoteFile=remoteFile.replaceAll("<server>", upld.getIdServer());
		handler.upload(localFile, remoteFile);
		switch (upld.getExecute())
		{
		case "Yes" :
			String command = "chmod 777 " + remoteFile;
			handler.executeCommand(command);
			command = ". "+remoteFile;
			handler.executeCommand(command);
			
			break;
		case "No" :
			break;
		}
	}

}
