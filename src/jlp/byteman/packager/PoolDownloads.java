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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PoolDownloads {
	public PoolDownloads() {
		int i = 1;
		boolean cont = true;
		while (cont) {
			if (Main.propsCnx.containsKey("download." + i + ".rank")) {
				if (Main.propsCnx.getProperty("download." + i + ".rank")
						.equals("0")) {
					
					String[] servers = Main.propsCnx.getProperty(
							"download." + i + ".idServer").split("(;| )");
					
					Set<String> realServers = new HashSet<String>();
					Set<DwldSSH> dwlds = new HashSet<DwldSSH>();
					ExecutorService pool = null;
					if (servers[0].equals("ALL")) {

						realServers.addAll(Main.allServers);
						for (String serv3 : Main.allServers) {
							dwlds.add(new DwldSSH("0", serv3, Main.propsCnx
									.getProperty("download." + i + ".remoteFiles"),
									Main.propsCnx.getProperty("download." + i
											+ ".localDirectory"), Main.propsCnx
											.getProperty("download." + i
													+ ".howmany"),Main.propsCnx
													.getProperty("download." + i
															+ ".action")));
						}
						// uplds.add(new UpldSSH("0", ));
					}

					else if (servers.length >= 1) {
						for (String serv : servers) {
							
							if (serv.contains("*")) {
								serv = serv.replaceAll("\\*", ".*");

							}
							
							for (String serv2 : Main.allServers) {
								
								if (serv2.matches(serv)) {
									realServers.add(serv2);
								}
							}
						}
						for (String serv3 : realServers) {
							dwlds.add(new DwldSSH("0", serv3, Main.propsCnx
									.getProperty("download." + i + ".remoteFiles"),
									Main.propsCnx.getProperty("download." + i
											+ ".localDirectory"), Main.propsCnx
											.getProperty("download." + i
													+ ".howmany"),Main.propsCnx
													.getProperty("download." + i
															+ ".action")));
						}
					}
					//
					if(realServers.size()==0) return;
					pool = Executors.newFixedThreadPool(realServers.size());
					
					for (DwldSSH dwld : dwlds) {
						DownloadService worker = new DownloadService(dwld);
						pool.execute(worker);

					}
					shutdownAndAwaitTermination(pool);
					System.out.println("PoolDownloads : Finished all threads");

				}
				i++;

			} else {
				cont = false;
			}
		}
	}

	public PoolDownloads(String key) {

		int idx=key.lastIndexOf(".");
		String pref=key.substring(0, idx);
		String[] servers = Main.propsCnx.getProperty(
				pref + ".idServer").split("(;| )");
		
		Set<String> realServers = new HashSet<String>();
		Set<DwldSSH> dwlds = new HashSet<DwldSSH>();
		ExecutorService pool = null;
		if (servers[0].equals("ALL")) {

			realServers.addAll(Main.allServers);
			for (String serv3 : Main.allServers) {
				dwlds.add(new DwldSSH("0", serv3, Main.propsCnx
						.getProperty(pref + ".remoteFiles"),
						Main.propsCnx.getProperty(pref
								+ ".localDirectory"), Main.propsCnx
								.getProperty(pref
										+ ".howmany"),Main.propsCnx
										.getProperty(pref
												+ ".action")));
			}
			// uplds.add(new UpldSSH("0", ));
		}

		else if (servers.length >= 1) {
			for (String serv : servers) {
				
				if (serv.contains("*")) {
					serv = serv.replaceAll("\\*", ".*");

				}
				
				for (String serv2 : Main.allServers) {
					
					if (serv2.matches(serv)) {
						realServers.add(serv2);
					}
				}
			}
			for (String serv3 : realServers) {
				dwlds.add(new DwldSSH("0", serv3, Main.propsCnx
						.getProperty(pref + ".remoteFiles"),
						Main.propsCnx.getProperty(pref
								+ ".localDirectory"), Main.propsCnx
								.getProperty(pref
										+ ".howmany"),Main.propsCnx
										.getProperty(pref
												+ ".action")));
			}
		}
		//
		if(realServers.size()==0) return;
		pool = Executors.newFixedThreadPool(realServers.size());
		
		for (DwldSSH dwld : dwlds) {
			DownloadService worker = new DownloadService(dwld);
			pool.execute(worker);

		}
		shutdownAndAwaitTermination(pool);
		System.out.println("PoolDownloads : Finished all threads");

	
	}
	
	
	void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(15, TimeUnit.MINUTES)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(15, TimeUnit.MINUTES))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
		}
	}

}
