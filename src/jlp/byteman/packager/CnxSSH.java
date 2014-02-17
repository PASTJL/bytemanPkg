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

public class CnxSSH implements Comparable<CnxSSH> {
	public CnxSSH(String _idServer, String _addrServer, String _port,
			String _login, String _password,String _rootpassword) {
		super();
		setIdServer(_idServer);
		setAddrServer(_addrServer);
		setPort ( _port);
		setLogin(_login);
		setPassword(_password);
		setRootpassword(_rootpassword);
	}
	private SimpleStringProperty idServer=new SimpleStringProperty("");
	private SimpleStringProperty addrServer=new SimpleStringProperty("");
	private SimpleStringProperty port=new SimpleStringProperty("");
	private SimpleStringProperty login=new SimpleStringProperty("");
	private SimpleStringProperty password=new SimpleStringProperty("");
	private SimpleStringProperty rootpassword=new SimpleStringProperty("");
	
	public String getIdServer() {
		return idServer.get();
	}
	public void setIdServer(String _idServer) {
		idServer.set( _idServer);
	}
	
	
	public String getAddrServer() {
		return addrServer.get();
	}
	public void setAddrServer(String _addrServer) {
		addrServer.set(_addrServer);
	}
	public String getPort() {
		return port.get();
	}
	public void setPort(String _port) {
		port.set(_port);
	}
	public String getLogin() {
		return login.get();
	}
	public void setLogin(String _login) {
		login.set(_login);
	}
	public String getPassword() {
		return password.get();
	}
	public void setPassword(String _password) {
		password.set(_password);
	}
	public String getRootpassword() {
		return rootpassword.get();
	}
	public void setRootpassword(String _rootpassword) {
		rootpassword.set(_rootpassword);
	}
	public int compareTo(CnxSSH o) {
		return this.getIdServer().compareTo(o.getIdServer());
		
	}

}
