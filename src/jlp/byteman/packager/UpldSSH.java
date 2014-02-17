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

import javafx.beans.property.SimpleStringProperty;

public class UpldSSH implements Comparable<UpldSSH> {
	public UpldSSH(String _rank,String _idServer, String _localFile, String _remoteDirectory,
			String _execute) {
		super();
		setIdServer(_idServer);
		setRank(_rank);
		setLocalFile ( _localFile);
		setRemoteDirectory(_remoteDirectory);
		setExecute(_execute);
	}
	private SimpleStringProperty idServer=new SimpleStringProperty("");
	private SimpleStringProperty rank=new SimpleStringProperty("");
	private SimpleStringProperty localFile=new SimpleStringProperty("");
	private SimpleStringProperty remoteDirectory=new SimpleStringProperty("");
	private SimpleStringProperty execute=new SimpleStringProperty("");
	
	public String getIdServer() {
		return idServer.get();
	}
	public void setIdServer(String _idServer) {
		idServer.set( _idServer);
	}
	
	
	public String getRank() {
		return rank.get();
	}
	public void setRank(String _rank) {
		rank.set(_rank);
	}
	public String getLocalFile() {
		return localFile.get();
	}
	public void setLocalFile(String _localFile) {
		localFile.set(_localFile);
	}
	public String getRemoteDirectory() {
		return remoteDirectory.get();
	}
	public void setRemoteDirectory(String _remoteDirectory) {
		remoteDirectory.set(_remoteDirectory);
	}
	public String getExecute() {
		return execute.get();
	}
	public void setExecute(String _execute) {
		execute.set(_execute);
	}
	
	public int compareTo(UpldSSH o) {
		return this.getIdServer().compareTo(o.getIdServer());
		
	}

}
