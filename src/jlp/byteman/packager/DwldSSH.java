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

public class DwldSSH implements Comparable<DwldSSH> {
	public DwldSSH(String _rank,String _idServer,String _remoteFiles, String _localDirectory, 
			String _howmany, String _action) {
		super();
		setIdServer(_idServer);
		setRank(_rank);
		setLocalDirectory ( _localDirectory);
		setRemoteFiles(_remoteFiles);
		setHowmany(_howmany);
		setAction(_action);
	}
	private SimpleStringProperty idServer=new SimpleStringProperty("");
	private SimpleStringProperty rank=new SimpleStringProperty("");
	private SimpleStringProperty remoteFiles=new SimpleStringProperty("");
	private SimpleStringProperty localDirectory=new SimpleStringProperty("");
	private SimpleStringProperty howmany=new SimpleStringProperty("");
	private SimpleStringProperty action=new SimpleStringProperty("");
	
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
	public String getLocalDirectory() {
		return localDirectory.get();
	}
	public void  setLocalDirectory(String _localDirectory) {
		localDirectory.set(_localDirectory);
	}
	
	public void setRemoteFiles(String _remoteFiles) {
		remoteFiles.set(_remoteFiles);
	}
	public String getRemoteFiles() {
		return remoteFiles.get();
	}
	public void setHowmany(String _howmany) {
		howmany.set(_howmany);
	}
	public String getHowmany() {
		return howmany.get();
	}
	public void setAction(String _action) {
		action.set(_action);
	}
	public String getAction() {
		return action.get();
	}
	public int compareTo(DwldSSH o) {
		return this.getIdServer().compareTo(o.getIdServer());
		
	}

}
