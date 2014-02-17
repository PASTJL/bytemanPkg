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

import javax.management.openmbean.CompositeDataSupport;

public class GenericBean implements GenericBeanMBean {

	// The GenericBean contains a field currentAge wich is the date of the last
	// visit or the counter of the last visit

	private String name;
	private Long currentAge = 0L;
	private CompositeDataSupport compositeDataSupport;
	private int numExecution = 0;

	public int hashCode() {
		return name.hashCode();
	}

	public boolean isEquals(GenericBean other) {
		return this.hashCode() == other.hashCode();
	}

	public GenericBean(String name) {
		this.name = name;

	}

	public Long getCurrentAge() {
		return currentAge;
	}

	public void setCurrentAge(Long currentAge) {
		this.currentAge = currentAge;
	}

	public String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public CompositeDataSupport getCompositeDataSupport() {

		return compositeDataSupport;
	}

	public synchronized void setCompositeDataSupport(CompositeDataSupport cds) {

		this.compositeDataSupport = cds;
	}

	public synchronized int getNumExecution() {
		// TODO Auto-generated method stub
		return numExecution;
	}

	public synchronized void setNumExecution(int nb) {
		// TODO Auto-generated method stub
		numExecution = nb;
	}

}