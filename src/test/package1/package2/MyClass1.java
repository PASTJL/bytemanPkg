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

public class MyClass1 {
	String name="default";
	Random ran=new Random();
	public MyClass1(String _name){
		name=_name;
	}
	public  static void myStaticMethod(int unInt, int otherInt){
		Random ran=new Random();
		int to=ran.nextInt()+otherInt;
		try {
			
			Thread.sleep(to);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String myCalledMethod(int unInt){
		try {
			
			Thread.sleep(unInt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return("depuis myCalledMethod");
	}
	
	public String myMethod3(int unInt){
		System.out.println("Entering MyClass1.myMethod3");
		System.out.println("avant appel de  MyClass1.myCalledMethod ");
		myCalledMethod(unInt);
		System.out.println("apres appel de  MyClass1.myCalledMethod ");
		try {
			
			Thread.sleep(unInt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("MyClass1.myMethod3 returning after :"+(unInt)+" ms");
		
		return "MyClass1.myMethod3" ;
	}
	public void myMethod2(int unInt){
		System.out.println("Entering MyClass1.myMethod2");
		int sl=ran.nextInt(2*unInt)-unInt;
		try {
			
			Thread.sleep(unInt+sl);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("MyClass1.myMethod2 returning after :"+(unInt+sl)+" ms");
		
		return ;
	}

	public int myMethod1(int unInt){
		System.out.println("Entering MyClass1.myMethod1");
		long deb=System.currentTimeMillis();
		int sl=ran.nextInt(2*unInt);
		try {
			
			Thread.sleep(sl);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<Integer.MAX_VALUE;i++){
			double j=Math.log((double) i);
		}
		System.out.println("MyClass1.myMethod1 returning after :"+(System.currentTimeMillis()-deb)+" ms");
		
		return unInt;
	}
	public int myMethod4(int unInt){
		long deb=System.currentTimeMillis();
		System.out.println("Entering MyClass1.myMethod4");
		int sl=ran.nextInt(2*unInt)-unInt;
		try {
			
			Thread.sleep(unInt+sl);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("MyClass1.myMethod4 returning sleep after :"+(unInt+sl)+" ms");
		System.out.println("MyClass1.myMethod4 returning System.currentTimeMillis after :"+(System.currentTimeMillis()-deb)+" ms");
		return unInt;
	}
}
