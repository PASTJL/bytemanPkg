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
package jlp.byteman.helper.tools;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.VirtualMachine;

//import ibm.tools.attach.*;

public class AttachJMX {
	public static void main(String args[]) throws Exception {

		if (args.length == 0) {
			System.err.println("Please provide process id");
			System.exit(-1);
		} else if (!args[0].matches("\\d+")) {
			System.err.println("The first argument is not a  process id");
			System.out.println("Usage : ./attach.sh $PID <operation>");
			System.out
					.println("The operations are :\n\t\treOpenOs\n\t\tenableRules\n\t\tdisableRules\n\t\tflushAllOs");
			System.exit(-1);
		}

		String provider = System.getProperty("PROVIDER", "HotSpot");

		VirtualMachine vm = null;
		String connectorAddr = null;
		if (provider.equalsIgnoreCase("HotSpot")) {
			vm = VirtualMachine.attach(args[0]);
			connectorAddr = vm.getAgentProperties().getProperty(
					"com.sun.management.jmxremote.localConnectorAddress");
			if (connectorAddr == null) {
				String agent = vm.getSystemProperties()
						.getProperty("java.home")
						+ File.separator
						+ "lib"
						+ File.separator + "management-agent.jar";
				vm.loadAgent(agent);
				connectorAddr = vm.getAgentProperties().getProperty(
						"com.sun.management.jmxremote.localConnectorAddress");
			}
		}
		else if (provider.equalsIgnoreCase("IBM")){
			System.out.println("Trying with IBM");

			//vm = ibm.tools.attach.J9VirtualMachine.attach(args[0]);
			Method method = Class.forName("ibm.tools.attach.J9VirtualMachine")
					.getSuperclass().getDeclaredMethod("attach", String.class);
			method.setAccessible(true);
			vm = (VirtualMachine) method.invoke(null, args[0]);
	        String agentIBM = "instrument,"
	                + vm.getSystemProperties().getProperty("java.home")
	                + File.separator + "lib" + File.separator
	                + "management-agent.jar=";
	        vm.loadAgentLibrary(agentIBM);

	        connectorAddr = vm.getSystemProperties().getProperty(
	        		"com.sun.management.jmxremote.localConnectorAddress");
	
	        System.out.println(" IBM connectorAddr ="+connectorAddr);
		}
		else {
			System.out.println("Unknown JVM provider");
			System.exit(1);
		}
		JMXServiceURL serviceURL = new JMXServiceURL(connectorAddr);
		JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
		String strObjName = "TraceByteman:type=trace";

		ObjectName objName = new ObjectName(strObjName);

		Set<ObjectName> mbeans = mbsc.queryNames(objName, null);
		if (mbeans.isEmpty()) {
			System.out
					.println("no mbean : TraceByteman:type=trace not registred");
			System.exit(-1);
		}
		if (args.length == 2) {
			if (args[1].equals("reOpenOs") || args[1].equals("enableRules")
					|| args[1].equals("disableRules")
					|| args[1].equals("flushAllOs")) {
				System.out.println("Invoking operation : " + args[1]);
				mbsc.invoke(objName, args[1], null, null);
			} else {
				System.out
						.println("The operation " + args[1] + " is not known");
				System.out
						.println("The correct operations are :\n\t\treOpenOs\n\t\tenableRules\n\t\tdisableRules\n\t\tflushAllOs");
			}
		} else {
			System.out.println("Usage : ./attach.sh $PID <operation>");
			System.out
					.println("The operations are :\n\t\treOpenOs\n\t\tenableRules\n\t\tdisableRules\n\t\tflushAllOs");
		}
		vm.detach();
	}
}
