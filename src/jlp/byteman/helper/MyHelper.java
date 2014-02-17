
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
package jlp.byteman.helper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import jlp.byteman.helper.utils.Trace;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

/**
 * @author JLP
 *
 */
public class MyHelper extends Helper {
	public static Trace trace =null;


	public static ThreadMXBean tMB = null;
	/**
	 * get the ThreadMXBean
	 * @return the ThreadMXBean
	 */
	public  ThreadMXBean gettMB() {
		synchronized(MyHelper.class){
		if(null == tMB){
			tMB = ManagementFactory.getThreadMXBean();
		}
		return tMB;
		}
	}

	static MBeanServer mbs = null;

	/**
	 * 
	 * @param rule
	 * Initialize a Trace singleton and the ThreadMXBean
	 */
	protected MyHelper(Rule rule) {
		super(rule);
		synchronized (MyHelper.class) {
			// retrieve Trace Singleton loaded by System Classloader of
			// javaagent.
			if (null == trace) {
				trace = Trace.getInstance();

				// Le declarer au MBeanServer
				tMB = ManagementFactory.getThreadMXBean();
				Locale.setDefault(Locale.ENGLISH);
				mbs = ManagementFactory.getPlatformMBeanServer();
				String strObjName = "TraceByteman:type=trace";

				ObjectName name;
				try {
					name = new ObjectName(strObjName);
					mbs.registerMBean(trace, name);
				} catch (MalformedObjectNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstanceAlreadyExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MBeanRegistrationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotCompliantMBeanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	/**
	 * @return
	 * return the Trace singleton
	 */
	public Trace getTrace() {
		return trace;
	}

	/**
	 * @return
	 * return the properties used by the script rule and/or the helper
	 */
	public Properties getProps() {
		return Trace.props;
	}

	/**
	 * the current format date is yyyy/MM/dd:HH:mm:ss.SSS
	 * @return
	 * return the current date
	 */
	public String currentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

	/**
	 * @param o Object attached to the counter
	 * calls Helper.incrementCounter(o)
	 */
	public synchronized void incrementMyCounter(Object o) {
		incrementCounter(o);
	}

	/**
	 * @param int1
	 * @param int2
	 * @return
	 * true is congruent ( int1 % int2 = 0)
	 */
	public  boolean isCongruent(int int1, int int2) {
		if (int1 % int2 == 0)
			return true;
		else
			return false;
	}

	
	/**
	 * @param long1
	 * @param long2
	 * @return
	 *  * true is congruent ( long1 % long2 = 0)
	 */
	public  boolean isCongruent(long long1, long long2) {
		if (long1 % long2 == 0)
			return true;
		else
			return false;
	}

	
	/**
	 * isGtOrEq => is value greater or equals to ref return true 
	 * @param value
	 * @param ref
	 * @return boolean
	 */
	public boolean isGtOrEq (long value, long ref ){
		
		if(value >= ref) return true;
		return false;
	}
	/**
	 * isGtOrEq => is value greater or equals to ref return true 
	 * @param value
	 * @param ref
	 * @return boolean
	 */
	public boolean isGtOrEq (Double value, Double ref ){
		
		if(value >= ref) return true;
		return false;
	}
	
	/**
	 * Sleep during a time.
	 * @param sleepms time to sleep in millis
	 * Sleep during sleepms ms
	 */
	public void myWaiter(long sleepms) {
		try {

			Thread.sleep(sleepms);
		} catch (InterruptedException e) {

		}

	}
	
	/**
	 * * Sleep during a randomized time.
	 * @param timems : the  time to compute the time to sleep  in millis ( random  between 0 and 2*timems)
	 * @return
	 * return the randomized  time 
	 */
	public long myRandomWaiter(long timems) {
		 return myRandomWaiter( timems, timems); 
		 
	}

	/**
	 * Sleep during a randomized time.
	 * @param timems : the middle time to compute the time to sleep  in millis ( random  between timems-var and timems+var)
	 * @param var : the variation time in millis. The case var > timems is treated as timems=var.
	 * @return the randomized  time 
	 */
	public long myRandomWaiter(long timems, long var) {
		Random ran=new Random();
		if(var > timems)var=timems;
		long offset=(long) (ran.nextInt((int)(2 * var)) - var);
		try {

			Thread.sleep(timems+offset);
		} catch (InterruptedException e) {

		}
		return timems+offset;
	}
	
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
		public long perCent(long num,long denum){
		if (denum == 0) return 0;
		return  (long) (100 * num)/denum;
	}
		/**
		 * @param num
		 * @param denum
		 * @return
		 * the num/denum *100 fraction in %
		 */	
	public long perCent(int num,int denum){
		if (denum == 0) return 0;
		return  100 * (long) (100 * num)/denum;
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public long perCent(Long num,Long denum){
		if (denum == 0) return 0;
		return (long) (long)(100 * num)/(long)denum;
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public long perCent(Integer num,Integer denum){
		if (denum == 0) return 0;
		return (long) (100 * num)/denum;
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public long perCent(double num,double denum){
		if (denum == 0) return 0;
		return (long) ((100 * num)/denum);
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public long perCent(Double num,Double denum){
		if (denum == 0) return 0;
		return (long) ((100 * num)/denum);
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public boolean getPrimBoolean(String strBool){
		if("true".equals(strBool)) return true;
		else
		return false;
	}
	/**
	 * @param num
	 * @param denum
	 * @return
	 * the num/denum *100 fraction in %
	 */
	public Boolean getBoolean(String strBool){
		if("true".equals(strBool))return new Boolean(true);
		else
		return Boolean.valueOf(false);
	}
	
	public boolean isEquals(Object obj1, Object obj2){
		if ( obj1 == null || obj2== null) return false;
		if (obj1.toString() .equals(obj2.toString())) return true;
		return false;
	}
	public boolean isEquals(int obj1, int obj2){
		
		return (obj1 == obj2);
	}
	public boolean isNull(Object obj){
		return (null == obj);
		
	}
	public boolean isNotNull(Object obj){
		return (null != obj);
		
	}
	
	public boolean myAnd(boolean exp1, boolean exp2){
		return  (exp1 && exp2);
	}
	public boolean myOr(boolean exp1, boolean exp2){
		return  (exp1 || exp2);
	}
	public boolean myNot(boolean exp1){
		return  !(exp1);
	}
	
	public boolean ecrisln(String str)
	{
		System.out.println(str);
		return true;
		
	}
	
	public java.util.Collection getEmptyCollection(String className){
		if (className.equals("java.util.ArrayList") )
		{
			ArrayList arrL=new ArrayList();
			
			return arrL;
		}
		else if (className.equals("java.util.LinkedList") )
		{
			return new java.util.LinkedList();
		}
		else if (className.equals("java.util.TreeSet") )
		{
			return new java.util.TreeSet();
		}
		
		else if (className.equals("java.util.Vector") )
		{
			return new java.util.Vector();
		}
		
		return null;
	}
	public Object getEmpty(String className){
		
		if(className.equals("java.lang.String")){
			return "";
		}
		else if (className.equals("java.util.ArrayList") )
		{
			return new ArrayList();
		}
		else if (className.equals("java.util.HashMap") )
		{
			return new HashMap();
		}
		
		return null;
		
	}
}
