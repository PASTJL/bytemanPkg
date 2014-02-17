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
import java.util.Set;
import java.util.regex.Pattern;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.VirtualMachine;

public class AttachJMXTest {
	public static void main(String args[]) throws Exception {

		
		 
		if (args.length == 0) {
			System.err.println("Please provide process id");
			System.exit(-1);
		}
		else if (!args[0].matches("\\d+")) {
				System.err.println("The first argument is not a  process id");
				System.exit(-1);
		}
		VirtualMachine vm = VirtualMachine.attach(args[0]);
		String connectorAddr = vm.getAgentProperties().getProperty(
				"com.sun.management.jmxremote");
		if (connectorAddr == null) {
			String agent = vm.getSystemProperties().getProperty("java.home")
					+ File.separator + "lib" + File.separator
					+ "management-agent.jar";
			vm.loadAgent(agent);
			connectorAddr = vm.getAgentProperties().getProperty(
					"com.sun.management.jmxremote");
		}
		vm.getAgentProperties().setProperty("com.sun.management.jmxremote.ssl", "false");
		vm.getAgentProperties().setProperty("com.sun.management.jmxremote.authenticate", "false");
		vm.getAgentProperties().setProperty("com.sun.management.jmxremote.port", "2500");
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
			mbsc.invoke(objName, args[1], null, null);
		} else if (args.length == 3) {

			if (args[2].equals("true") || args[2].equals("false")) {
				Boolean bool = args[2].equals("true") ? true : false;
				AttributeList attrList = new AttributeList();
				Attribute attr;
				attr = new Attribute("active", bool);
				attrList.add(attr);
				Object opParams[] = { attrList };
				String opSig[] = { attrList.getClass().getName() };
				mbsc.invoke(objName, args[1], opParams, opSig);
			}

		}
	}
}
