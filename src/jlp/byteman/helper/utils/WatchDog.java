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
import java.io.IOException;
import java.util.Map;


public class WatchDog extends Thread implements Runnable {
	
	
		

	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		System.out.println("Closing in WatchDog :"+ Version.version);
		System.out
				.println("[Instrumentation Rules by JBoss/Byteman  ] Received a control C");
		System.out
				.println("[Instrumentation Rules by JBoss/Byteman  ] Closing all OutpuStreams");
		try {
			for (Map.Entry<String, BufferedWriter> entry : Trace.hmBuffs
					.entrySet()) {
				entry.getValue().flush();
				entry.getValue().close();
				Thread.sleep(100);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("An operation of shutdown or flushing or reopening streams is pending");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
