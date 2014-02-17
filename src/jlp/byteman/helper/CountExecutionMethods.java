package jlp.byteman.helper;

import org.jboss.byteman.rule.Rule;

public class CountExecutionMethods extends MyHelper {
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}
	protected CountExecutionMethods(Rule rule) {
		super(rule);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"fullMethodName"+sep+"countExecutionMethods(unit)"+sep;
	}

}
