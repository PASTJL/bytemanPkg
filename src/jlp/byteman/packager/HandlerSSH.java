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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

public class HandlerSSH {
	public Session sess = null;
	String actionToDo;
	MySSHConnection myCon = null;
	int defaultTimeOut = 1000;

	public HandlerSSH(MySSHConnection myCon, int timeOut) {
		this.myCon = myCon;
		sess = getSession();
		defaultTimeOut = timeOut;

	}

	public Session getSession() {
		if (null != sess)
			return sess;
		else {
			JSch jSch = new JSch();

			try {
				sess = jSch.getSession(myCon.getUser(), myCon.getHost(),
						Integer.parseInt(myCon.getPort()));
				UserInfo ui = myCon;
				sess.setUserInfo(ui);
				// session.setServerAliveInterval(1000)

				sess.connect(defaultTimeOut);
				return sess;
			} catch (Throwable th) { // second Chance with timeout

				try {
					UserInfo ui = myCon;
					sess.setUserInfo(ui);
					// session.setServerAliveInterval(1000)

					sess.connect(1000);
					return sess;
				} catch (Throwable th2) {
					System.out
							.println("HandlerSSH => Can't open a session for "
									+ myCon.getHost() + " : "
									+ Integer.parseInt(myCon.getPort()));
					return null;
				}
			}
		}
	}

	public static int checkAck(InputStream in) {
		int b = 1;
		try {
			b = in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuilder sb = new StringBuilder();
			int c = 0;
			do {
				try {
					c = in.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	public void close() {

		if (null != sess && sess.isConnected()) {
			sess.disconnect();

		}
		sess = null;
	}

	public String upload(String localFile, String remoteFile) {
		return upload(
				localFile,
				remoteFile,
				Main.packager
						.getProperty("ssh.upload.command", "scp -p -t") + " ");
	}

	public String upload(String localFile, String remoteFile,
			String scpUploadCmd) {
		if (null != sess) {
			Channel channel = null;

			try {
				channel = sess.openChannel("exec");
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String command = scpUploadCmd + " " + remoteFile;
			System.out.println("HandlerSSH command=" + command);
			((ChannelExec) channel).setCommand(command);
			OutputStream out = null;
			InputStream in = null;
			try {
				out = channel.getOutputStream();

				in = channel.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!channel.isConnected())
				try {
					channel.connect();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (HandlerSSH.checkAck(in) != 0) {
				return null;
			}
			long filesize = (new File(localFile)).length();
			System.out.println("HandlerSSH filesize=" + filesize);
			command = "C0644 " + filesize + " ";
			if (localFile.lastIndexOf('/') > 0) {
				command += localFile.substring(localFile.lastIndexOf('/') + 1);
			} else {
				command += localFile;
			}
			command += "\n";

			try {
				out.write(command.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (HandlerSSH.checkAck(in) != 0) {
				return null;
			}

			FileInputStream fis = null;
			try {
				fis = new FileInputStream(localFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buf = new byte[10240];
			boolean bool = true;
			while (bool) {
				int len = 0;
				try {
					len = fis.read(buf, 0, buf.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (len <= 0)
					bool = false;
				try {
					out.write(buf, 0, len);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // out.flush();
			}
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// send '\0'
			buf[0] = 0;
			try {
				out.write(buf, 0, 1);
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (HandlerSSH.checkAck(in) != 0) {
				return null;
			}

			if (null != channel && !channel.isClosed()) {
				channel.disconnect();
				channel = null;
			}

			return null;
		} else
			return null;
	}

	public String executeCommand(String command,
			HashMap<String, String> hmProcess) {
		System.out
				.println("executeCommand(String command,HashMap<String,String> hmProcess) :\n myCon.getUser() => "
						+ myCon.getUser()
						+ "\n USER => "
						+ hmProcess.get("USER"));
		if (myCon.getUser().equals(hmProcess.get("USER"))) {
			return executeCommand(command);
		} else if (myCon.getUser().equals("root")) {
			// The target user is not root. Su to tager user must be done
			return executeCommandRootToUser(command, hmProcess);
		} else if (hmProcess.get("USER").equals("root")) {
			return executeCommandUserToRoot(command, hmProcess);
		} else {
			return executeCommandUser1ToUser2(command, hmProcess);
		}

	}

	private String executeCommandRootToUser(String command,
			HashMap<String, String> hmProcess) {
		System.out
		.println("executeCommand(String command,HashMap<String,String> hmProcess) => don't know user password, know root password");
		if (null != sess) {
			Channel channel = null;

			try {
				channel = sess.openChannel("exec");

			} catch (JSchException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			//Case : root → user 	with sudo, an only su may run also
			String cmd = "su - " + hmProcess.get("USER") + " -p -c \"" + command+"\"";

			((ChannelExec) channel).setPty(true);
			((ChannelExec) channel).setCommand("sudo -S -p '' " + cmd);
			System.out.println("HandlerSSH command to execute :sudo -S -p '' "
					+ cmd);
			channel.setInputStream(null);

			InputStream in = null;
			try {
				in = channel.getInputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			((ChannelExec) channel).setErrStream(System.err);
			// channel.asInstanceOf[ChannelExec].setOutputStream(System.out)
			((ChannelExec) channel).setPty(true);
			if (!channel.isConnected()) {

				try {
					channel.connect();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			boolean bool = true;
			byte[] tmp = new byte[10240000];
			int j = 0;
			String ret = "";

			while (bool) {

				try {
					while (in.available() > 0) {
						int i = -1;
						try {
							i = in.read(tmp, j, 1024);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// int i=in.read(tmp, j, 8);
						if (i < 0)
							bool = false;
						// System.out.print(new String(tmp, j, j + i));
						j = j + i;
						ret = new String(tmp, 0, j);

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (channel.isClosed()) {
					// System.out.println("exit-status: "+channel.getExitStatus());
					bool = false;
				}

				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
			if (null != channel && !channel.isClosed()) {
				channel.disconnect();
				channel = null;
			}

			// println("SSH Execute commande  ret="+ret)

			return ret;
		} else
			return null;

	}

	private String executeCommandUserToRoot(String command,
			HashMap<String, String> hmProcess) {

		System.out
				.println("executeCommandUserToRoot(String command,HashMap<String,String> hmProcess) => root login in local mode only, application running on root ");
		if (null != sess) {
			Channel channel = null;

			try {
				channel = sess.openChannel("exec");

			} catch (JSchException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			// channel.setInputStream(null);

			InputStream in = null;
			OutputStream out = null;
			try {
				in = channel.getInputStream();
				out = channel.getOutputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// ((ChannelExec) channel).setErrStream(System.err);
			((ChannelExec) channel).setErrStream(out);
			// channel.asInstanceOf[ChannelExec].setOutputStream(System.out)

			// try with su
			//Case : user(su) → root 
			boolean success = true;
			
			String cmd = "su  -p -c \"" + command+"\"";

			((ChannelExec) channel).setPty(true);
			((ChannelExec) channel).setCommand(cmd);
			System.out.println("HandlerSSH command to execute :" + cmd);
			if (!channel.isConnected()) {

				try {
					channel.connect();
					Thread.sleep(500);
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			boolean bool = true;
			byte[] tmp = new byte[10240000];
			int j = 0;
			String ret = "";
			try {
				out.write((this.myCon.getRootpassword() + "\n").getBytes());
				out.flush();

				while (bool) {

					try {
						while (in.available() > 0) {
							int i = -1;
							try {
								i = in.read(tmp, j, 1024);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// int i=in.read(tmp, j, 8);
							if (i < 0)
								bool = false;
							// System.out.print(new String(tmp, j, j + i));
							j = j + i;
							ret = new String(tmp, 0, j);

						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (channel.isClosed()) {
						// System.out.println("exit-status: "+channel.getExitStatus());
						bool = false;
					}
					
					if (ret.contains("su:") && 
							(ret.contains("password") ||ret.contains("mot de passe") )
							&& ret.contains("incorrect"))
					{
						success=false;
						throw new IOException("");
					}
				}
				

			} catch (IOException e2) {
				// TODO Auto-generated catch block
				System.out.println("echec with su -");
				success = false;
				if (null != channel && !channel.isClosed()) {
					channel.disconnect();
					channel = null;
				}
			}
			

			

			if (success == false) {
				success = true;
				channel = null;
				bool = true;
				tmp = new byte[10240000];
				 j = 0;
				ret = "";
				try {
					channel = sess.openChannel("exec");

				} catch (JSchException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				// channel.setInputStream(null);

				in = null;
				out = null;
				try {
					in = channel.getInputStream();
					out = channel.getOutputStream();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				// ((ChannelExec) channel).setErrStream(System.err);
				((ChannelExec) channel).setErrStream(out);
				// tentative avec sudo user dans sudoers pour su
				//Case : user(sudo su) → root 
				cmd = "sudo -p ' ' -S su  -p -c \"" + command+"\"";

				((ChannelExec) channel).setPty(true);
				((ChannelExec) channel).setCommand(cmd);
				System.out.println("HandlerSSH command to execute :" + cmd);
				if (!channel.isConnected()) {

					try {
						channel.connect();
						Thread.sleep(500);
					} catch (JSchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					out.write((this.myCon.getPassword() + "\n").getBytes());
					out.flush();

					while (bool) {

						try {
							while (in.available() > 0) {
								int i = -1;
								try {
									i = in.read(tmp, j, 1024);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// int i=in.read(tmp, j, 8);
								if (i < 0)
									bool = false;
								// System.out.print(new String(tmp, j, j + i));
								j = j + i;
								ret = new String(tmp, 0, j);

							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (channel.isClosed()) {
							// System.out.println("exit-status: "+channel.getExitStatus());
							bool = false;
						}

						// try {
						// Thread.sleep(1000);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }

					}
					if (ret.contains("not in the sudoers file") )
					{
						success=false;
						throw new IOException("");
					}
					
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					System.out.println("failed with sudo su -");
					success = false;
					ret=null;

				}

			
			

			if (null != channel && !channel.isClosed()) {
				channel.disconnect();
				channel = null;
			}
		}

			return ret;
		} else
			return null;

	}

	private String executeCommandUser1ToUser2(String command,
			HashMap<String, String> hmProcess) {



		System.out
				.println("executeCommandUser1ToUser2(String command,HashMap<String,String> hmProcess) => don't know user password, know root password ");
		if (null != sess) {
			Channel channel = null;

			try {
				channel = sess.openChannel("exec");

			} catch (JSchException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			// channel.setInputStream(null);

			InputStream in = null;
			OutputStream out = null;
			try {
				in = channel.getInputStream();
				out = channel.getOutputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// ((ChannelExec) channel).setErrStream(System.err);
			((ChannelExec) channel).setErrStream(out);
			// channel.asInstanceOf[ChannelExec].setOutputStream(System.out)

			// try if su is enabled on myCon.getUser(). By defeinition su is enabled on root
			//Case : user3(su) → root  → user 	don't know user password, know root password
			boolean success = true;
			String cmd = "su  -p -c " + "\'(su - "+hmProcess.get("USER")+" -c \""+ command+"\")\'";

			((ChannelExec) channel).setPty(true);
			((ChannelExec) channel).setCommand(cmd);
			System.out.println("HandlerSSH command to execute :" + cmd);
			if (!channel.isConnected()) {

				try {
					channel.connect();
					Thread.sleep(500);
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			boolean bool = true;
			byte[] tmp = new byte[10240000];
			int j = 0;
			String ret = "";
			try {
				out.write((this.myCon.getRootpassword() + "\n").getBytes());
				out.flush();

				while (bool) {

					try {
						while (in.available() > 0) {
							int i = -1;
							try {
								i = in.read(tmp, j, 1024);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// int i=in.read(tmp, j, 8);
							if (i < 0)
								bool = false;
							// System.out.print(new String(tmp, j, j + i));
							j = j + i;
							ret = new String(tmp, 0, j);

						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (channel.isClosed()) {
						// System.out.println("exit-status: "+channel.getExitStatus());
						bool = false;
					}
					
					if (ret.contains("su:") && 
							(ret.contains("password") ||ret.contains("mot de passe") )
							&& ret.contains("incorrect"))
					{
						success=false;
						throw new IOException("");
					}
				}
				

			} catch (IOException e2) {
				// TODO Auto-generated catch block
				System.out.println("failed with su -");
				success = false;
				if (null != channel && !channel.isClosed()) {
					channel.disconnect();
					channel = null;
				}
			}
			

			

			if (success == false) {
				success = true;
				channel = null;
				bool = true;
				tmp = new byte[10240000];
				 j = 0;
				ret = "";
				try {
					channel = sess.openChannel("exec");

				} catch (JSchException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				// channel.setInputStream(null);

				in = null;
				out = null;
				try {
					in = channel.getInputStream();
					out = channel.getOutputStream();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				// ((ChannelExec) channel).setErrStream(System.err);
				((ChannelExec) channel).setErrStream(out);
				// tentative avec sudo user3 dans sudoers pour su 
				//Case : user3(sudo su) → root  → user 	(don't know user password, know root password)
				
				 cmd = "sudo -p ' ' -S su  -p -c " +"\'(su - "+hmProcess.get("USER")+" -c \""+ command+"\")\'";
				((ChannelExec) channel).setPty(true);
				((ChannelExec) channel).setCommand(cmd);
				System.out.println("HandlerSSH command to execute :" + cmd);
				if (!channel.isConnected()) {

					try {
						channel.connect();
						Thread.sleep(500);
					} catch (JSchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					out.write((this.myCon.getPassword() + "\n").getBytes());
					out.flush();

					while (bool) {

						try {
							while (in.available() > 0) {
								int i = -1;
								try {
									i = in.read(tmp, j, 1024);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// int i=in.read(tmp, j, 8);
								if (i < 0)
									bool = false;
								// System.out.print(new String(tmp, j, j + i));
								j = j + i;
								ret = new String(tmp, 0, j);

							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (channel.isClosed()) {
							// System.out.println("exit-status: "+channel.getExitStatus());
							bool = false;
						}

						// try {
						// Thread.sleep(1000);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }

					}
					if (ret.contains("not in the sudoers file") )
					{
						success=false;
						throw new IOException("");
					}
					
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					System.out.println("failed with sudo su -");
					success = false;
					ret=null;

				}

			
			

			if (null != channel && !channel.isClosed()) {
				channel.disconnect();
				channel = null;
			}
		}

			return ret;
		} else
			return null;

	
	}

	public String executeCommand(String command) {
		if (null != sess) {
			Channel channel = null;

			try {
				channel = sess.openChannel("exec");
			} catch (JSchException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			((ChannelExec) channel).setCommand(command);
			System.out.println("HandlerSSH command to execute :" + command);
			channel.setInputStream(null);

			InputStream in = null;
			try {
				in = channel.getInputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			((ChannelExec) channel).setErrStream(System.err);
			// channel.asInstanceOf[ChannelExec].setOutputStream(System.out)
			((ChannelExec) channel).setPty(true);
			if (!channel.isConnected()) {

				try {
					channel.connect();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			boolean bool = true;
			byte[] tmp = new byte[10240000];
			int j = 0;
			String ret = "";

			while (bool) {

				try {
					while (in.available() > 0) {
						int i = -1;
						try {
							i = in.read(tmp, j, 1024);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// int i=in.read(tmp, j, 8);
						if (i < 0)
							bool = false;
						// System.out.print(new String(tmp, j, j + i));
						j = j + i;
						ret = new String(tmp, 0, j);

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (channel.isClosed()) {
					// System.out.println("exit-status: "+channel.getExitStatus());
					bool = false;
				}

				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}
			if (null != channel && !channel.isClosed()) {
				channel.disconnect();
				channel = null;
			}

			// println("SSH Execute commande  ret="+ret)

			return ret;
		} else
			return null;

	}

	public String download(String localFile, String remoteFile) {
		return download(localFile, remoteFile, false,
				Main.packager.getProperty("ssh.download.command", "scp -f")
						+ " ");
	}

	public String download(String localFile, String remoteFile,
			boolean compress, String scpDownloadCmd) {

		if (null != sess) {
			Channel channel = null;

			String remoteNew = remoteFile;
			String localNew = localFile;
			FileOutputStream fos = null;
			System.out.println("HandlerSSH download compress=" + compress);
			System.out.println("HandlerSSH download remoteFile=" + remoteFile
					+ "|");
			System.out.println("HandlerSSH download localFile=" + localFile
					+ "|");
			if ((!remoteFile.endsWith(".gz")) && compress) {
				String dir = remoteFile.substring(0,
						remoteFile.lastIndexOf("/"));
				String file = remoteFile
						.substring(remoteFile.lastIndexOf("/") + 1);
				String command = "cat  " + remoteFile + "| gzip > /tmp/" + file
						+ ".jva.gz";
				try {
					channel = sess.openChannel("exec");
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((ChannelExec) channel).setCommand(command);

				OutputStream out = null;
				InputStream in = null;
				try {
					out = channel.getOutputStream();
					in = channel.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				;

				try {
					channel.connect();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					out.write('\u0003');
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				byte[] tmp = new byte[1024000];
				int jj = 0;
				String ret = "";
				boolean bool = true;
				while (bool) {
					try {
						while (in.available() > 0) {
							int i = in.read(tmp, jj, 1024);
							// int i=in.read(tmp, j, 8);
							if (i < 0)
								bool = false;
							// System.out.print(new String(tmp, j, j+i));
							jj = jj + i;
							ret = new String(tmp, 0, jj);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (channel.isClosed()) {
						// System.out.println("exit-status: "+channel.getExitStatus());
						bool = false;
					}

					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

				}
				channel.disconnect();

				System.out.println("HandlerSSH Compression ok for : "
						+ remoteFile);
				remoteNew = "/tmp/" + file + ".jva.gz";
				localNew = localFile + ".gz";
			}

			try {
				channel = null;
				channel = sess.openChannel("exec");
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String command = scpDownloadCmd + " " + remoteNew;
			System.out.println("HandlerSSH command scp=" + command);
			((ChannelExec) channel).setCommand(command);

			OutputStream out = null;
			InputStream in = null;
			try {
				in = channel.getInputStream();
				out = channel.getOutputStream();
				channel.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] buf = new byte[2048];
			buf[0] = 0;
			try {
				out.write(buf, 0, 1);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			boolean bool = true;
			while (bool) {

				int c = HandlerSSH.checkAck(in);

				if (c != 'C') {

					bool = false;
				}

				if (bool) {

					// read '0644 '
					try {
						in.read(buf, 0, 5);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					long filesize = 0L;
					boolean bool2 = true;
					while (bool2) {

						try {
							if (in.read(buf, 0, 1) < 0) {
								// error
								bool2 = false;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (buf[0] == ' ') {

							bool2 = false;
						} else
							filesize = filesize * 10L + (long) (buf[0] - '0');
					}

					String file = null;
					boolean bool3 = true;
					int i = 0;
					while (bool3) {
						try {
							in.read(buf, i, 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (buf[i] == 0x0a) {
							file = new String(buf, 0, i);
							bool3 = false;
						}
						i += 1;
					}

					// System.out.println("filesize=" + filesize + ", file="
					// + file);

					// send '\0'
					buf[0] = 0;
					try {
						out.write(buf, 0, 1);
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// read a content of lfile
					try {
						fos = new FileOutputStream(localNew);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int foo = 0;
					boolean bool4 = true;
					while (bool4) {
						if (buf.length < filesize)
							foo = buf.length;
						else
							foo = (int) filesize;
						try {
							foo = in.read(buf, 0, foo);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (foo < 0) {
							// error
							bool4 = false;
						}
						if (bool4) {
							try {
								fos.write(buf, 0, foo);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							filesize -= foo;
							if (filesize == 0L)
								bool4 = false;
						}
					}
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fos = null;

					if (HandlerSSH.checkAck(in) != 0) {
						bool = false;
					}

					// send '\0'
					if (bool) {
						buf[0] = 0;
						try {
							out.write(buf, 0, 1);
							out.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}

			if (compress && remoteNew.endsWith(".jva.gz")) {

				// on efface le fichier .sca.gz
				command = "rm -f " + remoteNew;
				try {
					channel = sess.openChannel("exec");
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((ChannelExec) channel).setCommand(command);

				try {
					out = channel.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					in = channel.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					channel.connect();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					out.write('\u0003');
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// byte[] tmp=new byte[1024];
				// Augmente pour pouvoir lister un plus grand nombre de
				// fichiers
				// avec la commande ls
				byte[] tmp = new byte[1024000];
				int jj = 0;
				String ret = "";
				bool = true;
				while (bool) {
					try {
						while (in.available() > 0) {
							int i = in.read(tmp, jj, 1024);
							// int i=in.read(tmp, j, 8);
							if (i < 0)
								bool = false;
							// System.out.print(new String(tmp, j, j+i));
							jj = jj + i;
							ret = new String(tmp, 0, jj);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (channel.isClosed()) {
						// System.out.println("exit-status: "+channel.getExitStatus());
						bool = false;
					}

					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

				}
				System.out.println("HandlerSSH ret =" + ret
						+ " ; deleting  " + remoteNew);

			}

			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {

			}
			// sess.disconnect();
			return null;
		} else {
			System.out.println("Session is null");
			return null;
		}

	}

	public static void main(String[] args) {
		HandlerSSH handlerSSH = new HandlerSSH(new MySSHConnection("localhost",
				"22", "JLP", "pastjl1", "pastjl1"), 1000);

		// handlerSSH.upload(args[0], "/tmp/test.jlp");
		// handlerSSH.upload("/home/JLP/temptodwld.jar", "/tmp/temptodwld.jar");
		// System.out.println("upload termine");
		// handlerSSH.download(args[0] + "_local", "/tmp/test.jlp");
		// handlerSSH.download("/home/JLP/temptodwld.jar", "/tmp/todwld.jar");
		handlerSSH.download("/tmp/todwld.jar", "/home/JLP/temptodwld.jar");
		// System.out.println("download termine");
		handlerSSH.executeCommand("ls /tmp/*");

	}

}
