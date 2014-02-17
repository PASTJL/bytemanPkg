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
package test.package1.package2;

import java.util.Random;

public final class MainThreaded extends Thread{
	int id=0;
	long timer=1000;
	int boucles=10;
	public MainThreaded(int i,long timer,int boucles){
		id=i;
		this.timer=timer;
		this.boucles=boucles;
	}
	public void run(){
		MyClass1 myClass1=new MyClass1("jlp"+id);
		for (int j=0;j<boucles;j++){
		myClass1.myMethod1((int)timer);
		myClass1.myMethod3(1000+(int)timer);
		try {
			Thread.sleep(new Random().nextInt((int)timer));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nbThreads=Integer.parseInt(args[0]);
		long timer=Integer.parseInt(args[1]);
		int boucles=Integer.parseInt(args[2]);
		Thread[] threads=new MainThreaded[nbThreads];
		
		for(int i=0;i<nbThreads;i++){
			threads[i]=new MainThreaded(i,timer,boucles);
		}

		for(int i=0;i<nbThreads;i++){
			threads[i].start();
		}
		
		

	}

}
