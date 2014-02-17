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

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MySSHConnection    implements UserInfo ,UIKeyboardInteractive {

	private String host ;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setPasswords(String password) {
		this.password = password;
	}
	private String port ;
	private String user;
	private String password;
	private String rootpassword;
	
	public String getRootpassword() {
		return rootpassword;
	}
	public void setRootpassword(String rootpassword) {
		this.rootpassword = rootpassword;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	// passwords is 2 strings separated by a space, first string is the password of the user,
	// the second the root password if exists, by default the same as the user
	public MySSHConnection(String host , String port , String user , String password, String rootpassword ){
		this.host=host;
		this.port=port;
		this.user=user;
		this.password=password;
		this.rootpassword=rootpassword;
	}
	 public boolean  promptPassword(String message)
	    {
	     return  true;
	    }
	 public String  getPassword() {
	    return password;
	  }

	 public boolean promptPassphrase(String message)
	    {
	      return true;
	    }

	 public String getPassphrase() {

	   return null;
	  }
	 public boolean promptYesNo(String message) {
	    return true;
	  }
	  public void  showMessage(String message) {

	  }
	 public  String[] promptKeyboardInteractive(String destination,String name ,String instruction , String[] prompt, boolean[] echo) {
 
		 if(user.equals("root")) {
			 return new String[]{rootpassword};
		 }
		 else
	   return new String[]{password};

	  }

}
