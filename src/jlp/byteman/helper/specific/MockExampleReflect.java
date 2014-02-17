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
package jlp.byteman.helper.specific;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


// no  import a specific applicative Object.






import org.jboss.byteman.rule.Rule;

import jlp.byteman.helper.MyHelper;

public class MockExampleReflect extends MyHelper {
	
	protected MockExampleReflect(Rule rule) {
		super(rule);
		
	
	}

	public java.util.Collection getMock1Collection()
	{
		ArrayList arrL=new ArrayList();
		// Need of a specific applicative Object. 
		// The jar of the object must be in the classpath to compile
		// At runtime, on the target server you may add sys:<pathToJarcontainingtheObject> in the javaagent parameters
		
		Class<?> clazz = null;;
		try {
			clazz = Class.forName("jdbc_pool_basic.core.SimpleDTO",false,ClassLoader.getSystemClassLoader());
			Constructor ctor=clazz.getConstructor(String.class,Integer.TYPE);
			for(int i=0;i<10;i++){
			arrL.add(ctor.newInstance("Mock-Reflection-"+i,i));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return arrL;
	}




}
