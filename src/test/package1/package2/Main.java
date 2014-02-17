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

public final class Main {

	/**
	 * @param args
	 */
	public static void wait(int sleep){
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {

		int boucles=100;
		MyClass1 myClass1=new MyClass1("jlp");
		
		if (args.length>1){
			boucles=Integer.parseInt(args[1]);
		}
		for  (int i=0;i<boucles;i++) {
			int i1=myClass1.myMethod1(Integer.parseInt(args[0]));
			wait(Integer.parseInt(args[0]));
			myClass1.myMethod2(Integer.parseInt(args[0]));
			wait(Integer.parseInt(args[0]));
			//myClass1.myMethod3(Integer.parseInt(args[0]));
//			wait(Integer.parseInt(args[0]));
//			myClass1.myMethod4(Integer.parseInt(args[0]));
//			wait(Integer.parseInt(args[0]));
		}

	}

}
